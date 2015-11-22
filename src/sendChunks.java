import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Created by SampathYadav on 11/19/2015.
 */
public class SendChunks implements Runnable{
    Socket clientSocket;
    List<Chunk> MasterList;
    int numberOfChunks;
    String fileType;
    List<Integer> chunkIds;
    String RequestType;
    ObjectOutputStream outputStream;

    public SendChunks(Socket clientSocket, List<Chunk> masterList, int numberOfChunks,
                      String fileType, List<Integer> chunkIds, String RequestType,
                      ObjectOutputStream outputStream) {
        this.clientSocket = clientSocket;
        MasterList = masterList;
        this.numberOfChunks = numberOfChunks;
        this.fileType = fileType;
        this.chunkIds = chunkIds;
        this.RequestType = RequestType;
        this.outputStream = outputStream;
    }

    public void run(){
        // We Should Create a Object of FileOwnerToPeer and serialize it into a byte stream
        // For that Randomly Select a Number of Chunks - Let's 3 random chunks to be sent to the Peer
        int counter = 0;
        int size = MasterList.size();
        List<Chunk> chunks = new ArrayList<>();
        if(RequestType == "Server") {
            while (counter < 3) {
                // Select a Random Chunk from the MasterList
                Random random = new Random();
                int randomIndex = random.nextInt(size);
                chunks.add(MasterList.get(randomIndex));
                counter++;
            }
        }
        else
        {
            for(int i = 0; i < chunkIds.size(); i++)
            {
                chunks.add(MasterList.get(chunkIds.get(i)));
            }
        }

        // Object to be sent to the Client
        FileOwnerToPeer serverMessage = new FileOwnerToPeer(numberOfChunks,chunks,fileType);

        // For TESTING Purpose Only
//        FileOwnerToPeer serverMessage = new FileOwnerToPeer(numberOfChunks,MasterList);

        // Serialize this Object and send to Client
        try
        {
            // Get the Output stream of the Client Stream
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            // Write the Object in our Stream
            try {
                outputStream.writeObject(serverMessage);
                outputStream.flush();
            }
            catch(IOException e)
            {
                System.out.println("Cannot connect to Client OutputStream");
            }
            // Close the outputStream
        }catch(IOException i) {
            System.out.println("Cannot connect to Client OutputStream");
        }
    }
}
