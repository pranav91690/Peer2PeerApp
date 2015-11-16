import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by pranav on 11/8/15.
 */
public class sendChunks implements Runnable{
    // Instance Variables
    Socket clientSocket;

    // Constructor
    public sendChunks(Socket connection) {
        this.clientSocket = connection;
    }


    public void run(){
        // Create a Print Writer to print to the Client Socket
        try {
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);

            // Get the Required Chunks from the a Object

            out.println("Test Here!!!");

        }catch (IOException e){
            System.out.println("Handle the Exception");
        }
    }
}
