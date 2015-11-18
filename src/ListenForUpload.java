import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pranav on 11/18/15.
 */

// This is the thread that listens to the Upload Neighbour
public class ListenForUpload implements Runnable {
    ServerSocket peerListeningPort;

    public ListenForUpload(ServerSocket peerSocket) {
        this.peerListeningPort = peerSocket;
    }

    public void run(){
        try {
            // Keep Listening to the Client
            boolean keepListening = true;

            while (keepListening) {
                Socket uploadNeigbour = peerListeningPort.accept();

                /*
                Do Something Here -- Can be 2 things
                Send the Chunk ID List
                Send the Requested Chunks
                */
            }
        }catch (IOException e){
            // Handle this Exception
            System.out.println(e);
        }
    }
}
