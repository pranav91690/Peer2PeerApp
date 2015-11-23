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
public class Client {
    // The Input and Output Streams for the Client
    ObjectOutputStream  out;            //stream write to the socket
    ObjectInputStream   in;             //stream read from the socket

    // Client Listening Port
    int clientListeningPort;

    // File Owner Listening Port
    int serverListeningPort;

    // Download Neighbour Listening Port
    int downloadNeighbourPort;          //Get this from the BootStrap Server

    // Number of Chunks
    int numberOfChunks;                 //Number of Chunks in the Input File

    // Actual Chunk Data
    HashMap<Integer,Chunk> chunks;            //Present List of Chunks

    // Chunk Summary List
    HashSet<Integer> chunkIDs;          //Summary List of the Chunk ID's

    // File Type
    String fileType;

    // Received Chunks
    int rvdChunks;


    public static void main(String[] args) {
        // Create a Client Object
        Client client = new Client();

        // Instantiate the FileOwner and Client Listening Ports
        client.clientListeningPort = Integer.parseInt(args[0]);
        client.downloadNeighbourPort = Integer.parseInt(args[1]);
//        client.serverListeningPort = Integer.parseInt(args[1]);

//        client.clientListeningPort = Integer.parseInt(args[0]);
        client.serverListeningPort = 4000;

        // Connect to BootStrap Server and get the Download Neighbour Listening Port

        // Run the Client
        client.run();
    }

    void run(){
        // Step 1  -- Connect to the File Owner Server
        // -- Receive Chunks and setup the Client ID list
        Socket ServerSocket = null;
        try {
            // Create a Socket to connect to the Server
            ServerSocket = new Socket("localhost", serverListeningPort);

            try {
                // Initiate the Input and Output Buffer Streams for the Socket
                out = new ObjectOutputStream(ServerSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(ServerSocket.getInputStream());

                // Deserialize the Data Received From the Server Output Stream Here
                Object object = null;
                try {
                    System.out.println("Received Data from the Server");
                    object = in.readObject();
                    if (object instanceof FileOwnerToPeer) {
                        // Extract the Information and Store it
                        numberOfChunks = ((FileOwnerToPeer) object).numberOfChunks;
                        // Initiate the Array
                        chunks = new HashMap<>();
                        rvdChunks = ((FileOwnerToPeer) object).chunks.size();
                        // Update the List
                        for( Chunk c : ((FileOwnerToPeer) object).chunks){
                            chunks.put(c.chunkID, c);
                        }
                        fileType = ((FileOwnerToPeer) object).fileType;
                    }

                    // Create the Summary File and Update it
                    chunkIDs = new HashSet<>();
                    updateSummaryList();

                    // Step 2 -- Start the Server/Client Threads
                    // Create a Thread to Keep Listening on ClientListeningPort
                    Runnable r1 = new ListenForUpload(clientListeningPort,chunks,chunkIDs);
                    new Thread(r1).start();

                    // We Don't Exactly need a Thread for this
                    Runnable r2 = new ConnectToDownload(downloadNeighbourPort, chunks, chunkIDs,numberOfChunks,
                            clientListeningPort,fileType, rvdChunks);
                    new Thread(r2).start();


                } catch (IOException e) {
                    System.out.println("Cant Read Object");
                } catch (ClassNotFoundException e) {
                    System.out.println("Unrecognized Object Received from the Stream");
                }
            }catch (IOException e){
                System.out.println("Cannot Open Connection to the ServerPort");
            }

        }catch (IOException e){
            System.out.println("Sorry..Cannot Connect to the Server!");
        }
        finally {
            try {
                in.close();
                out.close();
                // Check on this later
                ServerSocket.close();
            }
            catch(IOException e) {
                System.out.println("Attempt to Close the Connection Failed");
            }
        }
    }

    public void updateSummaryList(){
        if(!chunks.isEmpty()) {
            for (Chunk c : chunks.values()) {
                chunkIDs.add(c.chunkID);
            }
        }
    }
}
