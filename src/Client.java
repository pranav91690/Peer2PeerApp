import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by pranav on 11/8/15.
 */
public class Client
{
    // --- Initialize the variables that are to be used

    ObjectOutputStream  out;            //stream write to the socket
    ObjectInputStream   in;             //stream read from the socket
    int clientListeningPort;            // Client Listening Port
    int serverListeningPort;            // File Owner Listening Port
    int downloadNeighbourPort;          //Get this from the BootStrap Server
    int numberOfChunks;                 //Number of Chunks in the Input File
    HashMap<Integer,Chunk> chunks;      //Present List of Chunks
    HashSet<Integer> chunkIDs;          //Summary List of the Chunk ID's
    String fileType;                    // File Type
    int rvdChunks;                      // Received Chunks


    public static void main(String[] args)
    {
        Client client = new Client();                           //Creating a client object
        client.clientListeningPort = Integer.parseInt(args[0]); // Instantiate the FileOwner and Client Listening Ports
        client.serverListeningPort = Integer.parseInt(args[1]); //Getting the port on which the server is listening
        client.run();                                           //Running the client
    }

    void run()
    {
        int dwnN = -1;                                          //Initiating Download neighbour port as -1
        Socket bootStrap = null;
        while (dwnN == -1)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                System.out.println(e);
            }

            try
            {
                bootStrap = new Socket("localhost",4100);
                System.out.println("Successfully Connected to the BootStrap Server");

                out = new ObjectOutputStream(bootStrap.getOutputStream());
                out.flush();
                in = new ObjectInputStream(bootStrap.getInputStream());

                out.writeInt(clientListeningPort);          //Sending the Client Listening Port
                out.flush();
                dwnN = in.readInt();
                if (dwnN != -1)
                {
                    downloadNeighbourPort = dwnN;
                }

                in.close();
                out.close();
                bootStrap.close();

            } catch (IOException e)
            {
                System.out.println("Cannot Communicate with the Client");
                break;
            }
        }

        System.out.println("Connect to Server");
        Socket ServerSocket = null;
        try
        {
            ServerSocket = new Socket("localhost", serverListeningPort);
            try
            {
                // Initiate the Input and Output Buffer Streams for the Socket
                out = new ObjectOutputStream(ServerSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(ServerSocket.getInputStream());
                Object object = null;
                try
                {
                    System.out.println("Received Data from the Server");
                    object = in.readObject();
                    if (object instanceof FileOwnerToPeer)
                    {
                        numberOfChunks = ((FileOwnerToPeer) object).numberOfChunks;
                        chunks = new HashMap<>();
                        rvdChunks = ((FileOwnerToPeer) object).chunks.size();
                        for( Chunk c : ((FileOwnerToPeer) object).chunks)
                        {
                            chunks.put(c.chunkID, c);
                        }
                        fileType = ((FileOwnerToPeer) object).fileType;
                    }
                    chunkIDs = new HashSet<>();
                    updateSummaryList();

                    System.out.println(chunkIDs);

                    //Starting a new thread to listen to the neighbour port to upload the desired chunks
                    Runnable r1 = new ListenForUpload(clientListeningPort,chunks,chunkIDs);
                    new Thread(r1).start();

                    //Starting a new thread to talk to the neighbour port to ask for the desired chunks
                    Runnable r2 = new ConnectToDownload(downloadNeighbourPort, chunks, chunkIDs,numberOfChunks,
                            clientListeningPort,fileType, rvdChunks);
                    new Thread(r2).start();
                }
                catch (IOException e)
                {
                    System.out.println("Cant Read Object");
                }
                catch (ClassNotFoundException e)
                {
                    System.out.println("Unrecognized Object Received from the Stream");
                }
            }
            catch (IOException e)
            {
                System.out.println("Cannot Open Connection to the ServerPort");
            }

        }catch (IOException e){
            System.out.println("Sorry..Cannot Connect to the Server!");
        }
    }

    public void updateSummaryList()
    {
        if(!chunks.isEmpty())
        {
            for (Chunk c : chunks.values())
            {
                chunkIDs.add(c.chunkID);
            }
        }
    }
}
