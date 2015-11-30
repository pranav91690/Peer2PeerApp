import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Created by SampathYadav on 11/19/2015.
 */
public class SendChunks implements Runnable{
    Socket clientSocket;        // Client Socket
    ArrayList<Chunk> chunks;    // List of Chunks
    int numberOfChunks;         // Number of Chunks
    String fileType;            // File Type
    ObjectOutputStream out;     // Output Stream
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
        ArrayList<Chunk> chunksToBeSent = new ArrayList<>();
        int noOfRandomChunks = (numberOfChunks/5);      //Deciding the number of chunks to send to each client

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

        for (int i=startIndex; i< endIndex; i++) {
            chunksToBeSent.add(this.chunks.get(i));
        }
        if(numberOfChunks%5 != 0)
        {
            noOfRandomChunks += 1;
        }

        if(noOfRandomChunks <3)
        {
            noOfRandomChunks = 3;
        }
        Collections.shuffle(list);
        for (int i=0; i< noOfRandomChunks; i++)
        {
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
