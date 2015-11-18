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

    // Download Neighbour Port
    int downloadNeighbourPort;

    // Number of Chunks
    Integer numberOfChunks;

    // List of Chunks
    List<Chunk> chunks;

    // Summary File
    // -- Chunks ID'S

    // Cleint should be listeing on a port
    // This is the Client class Object
    public static void main(String[] args) {
        // Create a Client Object
        Client client = new Client();
        client.run();
    }

    void run(){
        // Step 1  -- Create a Listening Port for Itself
        // -- Receive Back Download Neighbour Listening Port

        // Step 2  -- Connect to the File Owner Server
        // -- Receive Chunk ID List and (Some Chunks)
        try {
            // Create a Socket to connect to the Server
            Socket server = new Socket("localhost", 4000);

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
            System.out.println(e);
        }

        // Step 3 -- Start the Server/Client Threads
    }
}
