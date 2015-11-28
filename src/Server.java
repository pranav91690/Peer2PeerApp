import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by pranav on 11/8/15.
 */
public class Server {
    // Server Input and OutPut Streams
    ObjectOutputStream out;  //stream write to the socket

    // Master List of Chunk and their id's
    ArrayList<Chunk> chunks;
    int numberOfChunks;

    // Start a TCP Connection and make it listen to a port
    public static void main(String[] args){
        Server server = new Server();

        // Step 1 -- Split the File
        try {
            // We have to create a File Object from whatever file is given to us...Say this is given from args
            File file = new File("8.jpg");

            // Split the File
            server.splitFile(file);

            System.out.println(server.chunks.size());
            System.out.println(server.numberOfChunks);
            // Step 2 -- Run the Server and wait for incoming requests
            server.run(4000);
        }catch (Exception e){
            System.out.println("Cannot Open the File");
        }
    }

    public void run(int serverPort){
        ServerSocket server = null;
        try {
            server = new ServerSocket(serverPort);
            System.out.println("Server Started Listening on 4000");

            boolean keepRunning = true; //Keep Listening for Client Requests until the Server is not closed
            while (keepRunning) {
                try {
                    // Accept the clientSocket request from the client, (to which it is to send chunks)
                    Socket clientSocket = server.accept();

                    // Create a New Thread to Serve the Client
                    Runnable r = new SendChunks(clientSocket, chunks, numberOfChunks, "jpg", out);

                    // Start a new Thread with chunks
                    new Thread(r).start();

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
        int sizeOfFiles = 100 * 1024; // 100KB
        byte[] buffer;

        int fileSize = (int)file.length();

        FileInputStream inputStream;
        chunks = new ArrayList<>();

        try{
            // Read the Data into a File Stream
            inputStream = new FileInputStream(file);

            // Get the Name of the String
            String name = file.getName();

            // Variable to Store the Number of Bytes in a Chunk
            int chunkSize = 0;

            // While there is still data to read
            while(fileSize > 0){
                // Initiate the Buffer Array
                buffer = new byte[sizeOfFiles];

                // Read a chunk from the stream
                chunkSize = inputStream.read(buffer, 0, sizeOfFiles);

                // Decrease the File Size
                fileSize -= chunkSize;

                // Store the chunks in the Master List
                Chunk chunk = new Chunk(partCounter, buffer);
                chunks.add(chunk);
                numberOfChunks++;

                partCounter++;
            }
        }catch (IOException e){
            System.out.println("Cannot Read the File into a Stream");
        }

    }
}
