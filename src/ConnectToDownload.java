import java.io.IOException;
import java.net.Socket;

/**
 * Created by pranav on 11/18/15.
 */
public class ConnectToDownload implements Runnable {
    int downloadNeighbourPort;

    public ConnectToDownload(int downloadNeighbourPort) {
        this.downloadNeighbourPort = downloadNeighbourPort;
    }

    public void run(){
        try{
            Socket socket = new Socket("localhost", downloadNeighbourPort);

            // Request either the Chunk ID List

            // or the Missing chunks
        }catch (IOException e){
            // Handle this Exception
            System.out.println(e);
        }
    }
}
