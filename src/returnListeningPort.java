import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by pranav on 11/24/15.
 */
public class returnListeningPort implements Runnable {
    // Instance Variables
    Socket clientSocket;
    int downloadNeighbourPort;
    ObjectOutputStream out;

    public returnListeningPort(Socket clientSocket, int downloadNeighbourPort) {
        this.clientSocket = clientSocket;
        this.downloadNeighbourPort = downloadNeighbourPort;
    }

    public void run(){
        // Initiate the Object Input and Output Streams
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();

            // Send the Port Number to the Client
            try{
                out.writeInt(downloadNeighbourPort);
                out.flush();
            }catch (IOException e){
                System.out.println("Cannot Send Port to the Number");
            }
        }catch (IOException e){
            System.out.println("Cant Connect to Output/Output Streams");
        }
    }
}
