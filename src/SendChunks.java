import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Created by SampathYadav on 11/19/2015.
 */
public class SendChunks implements Runnable{
    // Client Socket
    Socket clientSocket;
    // List of Chunks
    ArrayList<Chunk> chunks;
    // Number of Chunks
    int numberOfChunks;
    // File Type
    String fileType;
    // Output Stream
    ObjectOutputStream out;

    public SendChunks(Socket clientSocket, ArrayList<Chunk> chunks, int numberOfChunks,
                      String fileType, ObjectOutputStream out) {
        this.clientSocket = clientSocket;
        this.chunks = chunks;
        this.numberOfChunks = numberOfChunks;
        this.fileType = fileType;
        this.out = out;
    }

    public void run(){
        // We Should Create a Object of FileOwnerToPeer and serialize it into a byte stream
        // For that Randomly Select a Number of Chunks - Let's 3 random chunks to be sent to the Peer
        ArrayList<Chunk> chunksToBeSent = new ArrayList<>();

        // Randomly Select 3 Chunks
        ArrayList<Integer> list = new ArrayList<>();
        for (int i=0; i<chunks.size(); i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        for (int i=0; i<3; i++) {
            int randomIndex = list.get(i);
            chunksToBeSent.add(this.chunks.get(randomIndex));
        }

        // Object to be sent to the Client
        FileOwnerToPeer serverMessage = new FileOwnerToPeer(numberOfChunks,chunksToBeSent,fileType);

        // Serialize this Object and send to Client
        try
        {
            // Get the Output stream of the Client Stream
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            // Write the Object in our Stream
            try {
                out.writeObject(serverMessage);
                out.flush();
            } catch(IOException e) {
                System.out.println("Cannot write to the Output Stream");
            }
        }catch(IOException i) {
            System.out.println("Cannot connect to Client OutputStream");
        }
    }
}
