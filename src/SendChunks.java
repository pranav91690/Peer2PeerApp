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
    int numberOfPeers;

    public SendChunks(Socket clientSocket, ArrayList<Chunk> chunks, int numberOfChunks,
                      String fileType,int numberOfPeers, ObjectOutputStream out) {
        this.clientSocket = clientSocket;
        this.chunks = chunks;
        this.numberOfChunks = numberOfChunks;
        this.fileType = fileType;
        this.out = out;
        this.numberOfPeers = numberOfPeers;
    }

    public void run(){
        // We Should Create a Object of FileOwnerToPeer and serialize it into a byte stream
        // For that Randomly Select a Number of Chunks - Let's 3 random chunks to be sent to the Peer
        ArrayList<Chunk> chunksToBeSent = new ArrayList<>();

        // Randomly Select 3 Chunks
        int noOfRandomChunks = (numberOfChunks/5);

        ArrayList<Integer> list = new ArrayList<>();
        for (int i=0; i<chunks.size(); i++) {
            list.add(i);
        }
        int startIndex = noOfRandomChunks*(numberOfPeers-1);
        int endIndex;
        if(numberOfPeers == 5)
        {
            endIndex = numberOfChunks;
        }
        else
        {
            endIndex = (noOfRandomChunks*numberOfPeers);
        }

        System.out.println(startIndex);
        for (int i=startIndex; i< endIndex; i++) {
            chunksToBeSent.add(this.chunks.get(i));
        }
        if(numberOfChunks%5 != 0)
        {
            noOfRandomChunks += 1;
        }

        //System.out.println(noOfRandomChunks);
        if(noOfRandomChunks <3)
        {
            noOfRandomChunks = 3;
        }
        Collections.shuffle(list);
        for (int i=0; i< noOfRandomChunks; i++) {
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
