import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by pranav on 11/18/15.
 */
public class ConnectToDownload implements Runnable {
    Socket clientSocket;
    int downloadNeighbourPort;
    List<Integer> neighbourChunkList;
    List<Chunk> MasterList;
    List<Integer> missingChunks;
    List<Integer> chunkIDs;

    public ConnectToDownload(int downloadNeighbourPort, Socket clientSocket , List<Integer> neighbourChunkList)
    {
        this.downloadNeighbourPort = downloadNeighbourPort;
        this.neighbourChunkList = neighbourChunkList;
        this.clientSocket = clientSocket;
    }

    public void run(){
        try
        {
            Socket server = new Socket("localhost",6000); //TODO: Have to make the port number dynamic
            //Using a callable service to get the chunks present in this client
            ExecutorService service = Executors.newSingleThreadExecutor();
            Callable<List<Chunk>> chunkList = new GetChunksForThisClient(6000);
            Future<List<Chunk>> future = service.submit(chunkList);
            MasterList = future.get();
            GetMissingChunks();         //Getting missing Chunks
            Runnable r = new SendChunks(clientSocket, MasterList, missingChunks.size(), "pdf", missingChunks);
            new Thread(r).start();      //Start a new Thread with MasterList
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        catch (InterruptedException e)
        {
            System.out.println(e);
        }
        catch (ExecutionException e)
        {
            System.out.println(e);
        }
    }
    public void GetMissingChunks()
    {
        if(!chunkIDs.isEmpty())
        {
            for(int i = 0; i < chunkIDs.size(); i++)
            {
                if(!neighbourChunkList.contains(chunkIDs.get(i)))
                    missingChunks.add(chunkIDs.get(i));
            }
        }
    }
}
