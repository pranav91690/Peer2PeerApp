import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pranav on 11/8/15.
 */
public class Server {
    // Server Input and OutPut Streams
    ObjectOutputStream out;  //stream write to the socket
    ObjectInputStream in;    //stream read from the socket

    // Master List of Chunk and their id's
    List<Chunk> MasterList;
    int numberOfChunks;



    // Start a TCP Connection and make it listen to a port
    public static void main(String[] args){
        Server server = new Server();

        // Step 1 -- Split the File
        try {
            // We have to create a File Object from whatever file is given to us...Say this is given from args
            File file = new File("Project3.pdf");

            // Split the File
            server.splitFile(file);

            // Start Listening on Server Port -- This might be a constant or given as command line argument

        }catch (Exception e){
            System.out.println(e);
        }

        // Step 2 -- Run the Server and wait for incoming requests
        server.run(4000);
    }

    public void run(int serverPort){
        try {
            // Create a new clientSocket on which the Server listens
            ServerSocket server = new ServerSocket(serverPort);

            System.out.println("Server Started Listening on 4000");

            // Keep Listening for Client Requests until the Server is not closed
            boolean keepRunning = true;
            while (keepRunning) {
                try {
                    // Accept the clientSocket request from the client, (to which it is to send chunks)
                    Socket clientSocket = server.accept();

                    // Create a New Thread to Serve the Client
                    Runnable r = new SendChunks(clientSocket, MasterList, numberOfChunks);

                    // Start a new Thread with MasterList
                    new Thread(r).start();

                } catch (IOException e) {
                    System.out.println("Cannot Accept Client Connection");
                }
            }
        }catch (IOException e){
            System.out.println("Server Cannot Be Started");
        }

        System.out.println("Server Closed");
    }

    public void splitFile(File file){
        int partCounter = 1;
        int sizeOfFiles = 100 * 1024; // 100KB
        byte[] buffer;

        int fileSize = (int)file.length();
        FileInputStream inputStream;

        MasterList = new ArrayList<>();

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

                //Write each chunk of data into separate file with different number in name
                File newFile = new File(file.getParent(), name + "." + String.format("%03d", partCounter));

                //Create an File Output Stream
                FileOutputStream outputStream = new FileOutputStream(newFile);
                outputStream.write(buffer);
                outputStream.close();

                // Store the MasterList in the Master List
                Chunk chunk = new Chunk(partCounter, newFile);
                MasterList.add(chunk);
                numberOfChunks++;

                partCounter++;
            }
        }catch (IOException e){
            System.out.println(e);
        }

    }


    public void mergeFiles() throws IOException{
        // Create a New File Output Stream at this path
        FileOutputStream fos = new FileOutputStream("merged.pdf");
        try(BufferedOutputStream mergeStream = new BufferedOutputStream(fos)){
            for(Chunk c : MasterList){
                // Merge The Files into a Single Stream
                Files.copy(c.file.toPath(), mergeStream);
            }

            // Close the Merge Stream
            mergeStream.close();
        }

    }
}
