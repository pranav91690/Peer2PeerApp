import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
    int numberOfChunks;   //Number of Chunks in the Input File

    // List of Chunks
    List<Chunk> chunks;       //Present List of Chunks
    List<Integer> neighbourChunks;
    List<Integer> missingChunks;
    List<Integer> chunkIDs;   //Summary List of the Chunk ID's
    int currentChunks; // Current Number of Chunks with us



    public static void main(String[] args) {
        // Create a Client Object
        Client client = new Client();

        // Instantiate the FileOwner and Client Listening Ports
        //client.clientListeningPort = Integer.parseInt(args[0]);
        //client.serverListeningPort = Integer.parseInt(args[1]);

        client.clientListeningPort = Integer.parseInt(args[0]);
        client.serverListeningPort = 4000;

        // Connect to BootStrap Server and get the Download Neighbour Listening Port

        // Run the Client
        client.run();

        // Wait Here
        System.out.println("Stop Here");
    }

    void run(){
        // Step 2  -- Connect to the File Owner Server
        // -- Receive Chunk ID List and (Some Chunks)
        Socket server = null;
        try {
            // Create a Socket to connect to the Server
            server = new Socket("localhost", serverListeningPort);

            // Initiate the Input and Output Buffer Streams for the Socket
            out = new ObjectOutputStream(server.getOutputStream());
            out.flush();
            in = new ObjectInputStream(server.getInputStream());
            // Deserialize the Data Received From the Server Output Stream Here

            Object object = null;
            try {
                object = in.readObject();
                if(object instanceof FileOwnerToPeer){
                    // Extract the Information and Store it
                    numberOfChunks = ((FileOwnerToPeer) object).numberOfChunks;
                    chunks = ((FileOwnerToPeer) object).chunks;
                }
                // Create the Summary File and Update it
                chunkIDs = new ArrayList<>();
                updateSummaryList();

                // Step 3 -- Start the Server/Client Threads
                // Create a Thread to Keep Listening on ClientListeningPOrt

            /*System.out.println(chunkIDs.size());
             while(5 > chunkIDs.size()) {
                 Runnable download = new ConnectToDownload(4000, server, chunkIDs);
                 new Thread(download).start();

                 updateSummaryList();
             }

            //Create a Thread to Connect to Download Neighbour
             Runnable upload = new ListenForUpload(clientListeningPort);
             new Thread(upload).start();
            */
                if(5 == chunkIDs.size())
                {
                    mergeFiles();
                }
            }catch (IOException e){
                System.out.println("Cant Read Object");
            }

        }catch (IOException e){
            System.out.println("Sorry..Cannot Connect to the Server!");
        }catch (ClassNotFoundException e) {
            System.out.println("Unrecognized Object Received from the Stream");
        }
        finally {
            try {
                in.close();
                out.close();
                server.close();
            }
            catch(IOException e) {
                System.out.println("Sorry..Cannot Connect to the Server!");
            }
        }
    }

    public void updateSummaryList(){
        if(!chunks.isEmpty()) {
            for (Chunk c : chunks) {
                chunkIDs.add(c.chunkID);
            }
        }
    }

    public void mergeFiles() throws IOException{
        // Create a New File Output Stream at this path
        FileOutputStream fos = new FileOutputStream("merged.pdf");
        try(BufferedOutputStream mergeStream = new BufferedOutputStream(fos)){
            // Sort the Client According to the Client ID Numbers before Combining
            for(Chunk c : chunks){
                mergeStream.write(c.bytes);
            }

            // Close the Merge Stream
            mergeStream.close();
        }

    }
}
