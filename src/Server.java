import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pranav on 11/8/15.
 */
public class Server {
    // Server Class
    // Let's say we have a file -> here

    // Start a TCP Connection and make it listen to a port
    public static void main(String[] args) throws IOException {
        // Create a new clientSocket on which the Server listens
        ServerSocket server = new ServerSocket(4000);

        System.out.println("Server Started Listening on 4000");

        // Keep Listening for Client Requests until the Server is not closed
        while (!server.isClosed()) {
            // This is thread to accept a new Connection
            try {
                // Accept the clientSocket request from the client, (which is to send it chunks)
                Socket clientSocket = server.accept();

                // Create a New Thread to Serve the Client
                Runnable r = new sendChunks(clientSocket);
                // Start a new Thread
                new Thread(r).start();

            }catch(IOException e) {
                System.out.println("Exception");
            }
        }

        System.out.println("Server Closed");


    }
}
