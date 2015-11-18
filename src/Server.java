import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
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
            // We have to create a File Object from whatever file is given to us...
            File file = new File("Project3.pdf");

            // Split the File
            server.splitFile(file);
        }catch (Exception e){
            System.out.println(e);
        }

        // Step 2 -- Run the Server and wait for incoming requests
        server.run();
    }

    void run(){
        // Split the Files into Chunks -- Find a way to randomly select chunks
        // and send them to each requesting client

        try {
            // Create a new clientSocket on which the Server listens
            ServerSocket server = new ServerSocket(4000);

            System.out.println("Server Started Listening on 4000");

            // Keep Listening for Client Requests until the Server is not closed
            while (!server.isClosed()) {
                // This is thread to accept a new Connection
                try {
                    // Accept the clientSocket request from the client, (to which it is to send chunks)
                    Socket clientSocket = server.accept();

                    // Create a New Thread to Serve the Client
                    Runnable r = new SendChunks(clientSocket, MasterList, numberOfChunks);

                    // Start a new Thread with MasterList
                    new Thread(r).start();

                } catch (IOException e) {
                    System.out.println("Exception");
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }

        System.out.println("Server Closed");
    }

    public void splitFile(File file){
        int partCounter = 1;
        int sizeOfFiles = 100 * 1024 * 1024; // 100KB
        byte[] buffer = new byte[sizeOfFiles];

        try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))){
            // Get the Name of the String
            String name = file.getName();

            int chunkSize = 0;
            // While there is still data to read
            while((chunkSize = inputStream.read(buffer)) > 0){
                //write each chunk of data into separate file with different number in name
                File newFile = new File(file.getParent(), name + "." + String.format("%03d", partCounter++));
                try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                    outputStream.write(buffer, 0, chunkSize);
                    // Store the MasterList in the Master List
                    Chunk chunk = new Chunk(partCounter, newFile);
                    MasterList.add(chunk);
                    numberOfChunks++;
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }

    }


    public void mergeFiles(List<File> files, File mergedFile) throws IOException{
        try(BufferedOutputStream mergeStream = new BufferedOutputStream(new FileOutputStream(mergedFile))){
            for(File f : files){
                // Merge The Files into a Single Stream
                Files.copy(f.toPath(), mergeStream);
            }
        }
    }
}
