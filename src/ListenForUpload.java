import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pranav on 11/18/15.
 */

// This is the thread that listens to the Upload Neighbour
public class ListenForUpload implements Runnable {
    ServerSocket peerSocket;

    public ListenForUpload(int peerListeningPort) {
        // Create a New Socket
        try {
            this.peerSocket = new ServerSocket(peerListeningPort);
        }catch (IOException e){
            System.out.println("Cannot Create a Peer Socket");
        }
    }

    public void run(){
        try {
            // Keep Listening to the Client
            boolean keepListening = true;

            while (keepListening) {
                Socket uploadNeigbour = peerSocket.accept();

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
