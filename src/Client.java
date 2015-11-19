import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by pranav on 11/8/15.
 */
public class Client {
    // The Input and Output Streams for the Client
    ObjectOutputStream  out;  //stream write to the socket
    ObjectInputStream   in;   //stream read from the socket

    // Client Listening Port
    int clientListeningPort;

    // File Owner Listening Port
    int serverListeningPort;


    // Download Neighbour Listening Port
    int downloadNeighbourPort;//Get this from the BootStrap Server

    // Number of Chunks
    Integer numberOfChunks;   //Number of Chunks in the Input File

    // List of Chunks
    List<Chunk> chunks;       //Present List of Chunks

    // Summary File
    List<Integer> chunkIDs;   //Summary List of the Chunk ID's

    public static void main(String[] args) {
        // Create a Client Object
        Client client = new Client();

        // Instantiate the FileOwner and Client Listening Ports
//        client.clientListeningPort = Integer.parseInt(args[0]);
//        client.serverListeningPort = Integer.parseInt(args[1]);

        client.clientListeningPort = 5000;
        client.serverListeningPort = 4000;

        // Connect to BootStrap Server and get the Download Neighbour Listening Port

        // Run the Client
        client.run();
    }

    void run(){
        // Step 2  -- Connect to the File Owner Server
        // -- Receive Chunk ID List and (Some Chunks)
        try {
            // Create a Socket to connect to the Server
            Socket server = new Socket("localhost", serverListeningPort);

            // Initiate the Input and Output Buffer Streams for the Socket
            out = new ObjectOutputStream(server.getOutputStream());
            out.flush();
            in = new ObjectInputStream(server.getInputStream());

            // Deserialize the Data Received From the Server Output Stream Here
            Object object = in.readObject();
            if(object instanceof FileOwnerToPeer){
                // Extract the Information and Store it
                numberOfChunks = ((FileOwnerToPeer) object).numberOfChunks;
                chunks = ((FileOwnerToPeer) object).chunks;
            }
        }catch (IOException e){
            System.out.println("Sorry..Cannot Connect to the Server!");
        }catch (ClassNotFoundException e){
            System.out.println("Unrecognized Object Received from the Stream");
        }

        // Step 3 -- Start the Server/Client Threads
    }
}
