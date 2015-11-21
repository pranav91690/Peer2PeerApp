import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by SampathYadav on 11/19/2015.
 */
public class GetChunksForThisClient implements Callable{
    // The Input and Output Streams for the Client
    ObjectOutputStream  out;    //stream write to the socket
    ObjectInputStream   in;     //stream read from the socket
    List<Integer> portList;     //HardCoding Ports being used for now
    int clientListeningPort;    //Client Listening Port
    int serverListeningPort;    //File Owner Listening Port
    int downloadNeighbourPort;  // Download Neighbour Listening Port --TODO://Get this from the BootStrap Server
    int totalnumberOfChunks;    //Number of Chunks in the Input File
    List<Chunk> chunks;         //Present List of Chunks
    List<Integer> chunkIDs;     //Summary List of the Chunk ID's
    int numberOfCurrentChunks;  // Current Number of Chunks with us
    int numberOfChunks;         //Number of Chunks in the Input File\
    int PortNumber;
    public GetChunksForThisClient(int PortNumber)
    {
        this.PortNumber = PortNumber;
    }
    public List<Chunk> call()throws Exception{
        // Step 2  -- Connect to the File Owner Server
        // -- Receive Chunk ID List and (Some Chunks)
        try {
            // Create a Socket to connect to the Server
            Socket server = new Socket("localhost", PortNumber);

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
        }catch (ClassNotFoundException e) {
            System.out.println("Unrecognized Object Received from the Stream");
        }
        return chunks;
    }
}

