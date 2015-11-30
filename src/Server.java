import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * Created by pranav on 11/8/15.
 */
public class Server {
    ObjectOutputStream out;     //stream write to the socket
    ArrayList<Chunk> chunks;    // Master List of Chunk and their id's
    int numberOfChunks;

    public static void main(String[] args){
        Server server = new Server();
        //-- Step 1 -- Split the File
        try
        {
            File file = new File(args[1]);                  //Passing the path of the file to be transferred
            server.splitFile(file);                         // Split the File
            // -- Step 2 -- Run the Server and wait for incoming requests
            server.run(Integer.parseInt(args[0]),args[2]);
        }
        catch (Exception e)
        {
            System.out.println("Cannot Open the File");
        }
    }

    public void run(int serverPort, String fileExtension){
        ServerSocket server = null;
        try {
            server = new ServerSocket(serverPort);
            System.out.println("Server Started Listening on " + serverPort);

            boolean keepRunning = true; //Keep Listening for Client Requests until the Server is not closed
            int numberOfPeers = 0;
            int numberOfPeersMod = 0;
            while (keepRunning) {
                try {
                    // Accept the clientSocket request from the client, (to which it is to send chunks)
                    Socket clientSocket = server.accept();
                    numberOfPeers++;
                    numberOfPeersMod = numberOfPeers % 5;
                    if(numberOfPeersMod == 0)
                        numberOfPeersMod = 5;

                    // Create a New Thread to Serve the Client
                    Runnable r = new SendChunks(clientSocket, chunks, numberOfChunks, fileExtension,numberOfPeersMod, out);
                    new Thread(r).start();  //Start a new Thread with chunks

                } catch (IOException e) {
                    System.out.println("Cannot Accept Client Connection");
                } catch (NullPointerException e){
                    System.out.println("Null Point Exception While Accepting Client Connection");
                }
            }
        }catch (IOException e){
            System.out.println("Server Cannot Be Started");
        }
    }

    public void splitFile(File file){
        int partCounter = 1;
        int sizeOfFiles = 100 * 1024;                                   //Breaking the file into chunks of size 100KB
        byte[] buffer;

        int fileSize = (int)file.length();

        FileInputStream inputStream;
        chunks = new ArrayList<>();

        try{
            // Read the Data into a File Stream
            inputStream = new FileInputStream(file);
            int chunkSize = 0;                                          // Variable to Store the Number of Bytes in a Chunk
            while(fileSize > 0){
                buffer = new byte[sizeOfFiles];                         // Initiate the Buffer Array
                chunkSize = inputStream.read(buffer, 0, sizeOfFiles);   // Read a chunk from the stream
                fileSize -= chunkSize;                                  // Decrease the File Size
                Chunk chunk = new Chunk(partCounter, buffer);           // Store the chunks in the Master List
                chunks.add(chunk);
                numberOfChunks++;
                partCounter++;
            }
        }catch (IOException e){
            System.out.println("Cannot Read the File into a Stream");
        }

    }
}
