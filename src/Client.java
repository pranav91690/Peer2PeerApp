import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by pranav on 11/8/15.
 */
public class Client {
    // This is the Client class
    public static void main(String[] args) throws IOException{
        // Connect to the File Owner Server/BootStrap Server in our case
        try {
            // Create a Socket to connect to the Server
            Socket serverSocket = new Socket("localhost", 4000);

            // Use a BufferedReader Object to capture the server's input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            //

        }catch (IOException e){
            System.out.println("Sorry..Cannot Connect to the Server!");
        }
    }
}
