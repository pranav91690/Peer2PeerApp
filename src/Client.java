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
    HashMap<Integer,Chunk> chunks;      //Present List of Chunks

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

        // Register with the BootStrap Server, Give the Listening Port and the Receive the
        // Download Neighbour Port

        //client.downloadNeighbourPort = Integer.parseInt(args[1]);

        client.serverListeningPort = 4000;

        // Run the Client
        client.run();
    }

    void run(){
        // Connect to BootStrap Server. Send the Listening Port and get the Download Neighbour Listening Port
        // Initiate the Input and Output Buffer Streams for the Socket
        // Send the Listening Port and Get the Download Neighbour Port
        // Initiate the Input and Output Buffer Streams for the Socket
        int dwnN = -1;
        Socket bootStrap = null;
        while (dwnN == -1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            try {
                bootStrap = new Socket("localhost",4100);
                System.out.println("Successfully Connected to the BootStrap Server");

                out = new ObjectOutputStream(bootStrap.getOutputStream());
                out.flush();
                in = new ObjectInputStream(bootStrap.getInputStream());

                // Send the Client Listening Port
                System.out.println("hi");
                out.writeInt(clientListeningPort);
                out.flush();
                dwnN = in.readInt();
                System.out.println(dwnN);
                if (dwnN != -1) {
                    System.out.println(dwnN);
                    downloadNeighbourPort = dwnN;
                }

                in.close();
                out.close();
                bootStrap.close();

            } catch (IOException e) {
                System.out.println("Cannot Communicate with the Client");
                break;
            }
        }

        System.out.println("Connect to Server");
        // Start the Client Process Now
        // Step 1  -- Connect to the File Owner Server -- Receive Chunks and setup the Client ID list
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

                    // Get the Download Neighbour From the BootStrap Server
                    // Start this Thread only if we get a non negative value
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
    }

    public void updateSummaryList(){
        if(!chunks.isEmpty()) {
            for (Chunk c : chunks.values()) {
                chunkIDs.add(c.chunkID);
                System.out.print(c.chunkID);
            }
            System.out.println("");
        }
    }
}
