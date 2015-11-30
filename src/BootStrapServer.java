import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by pranav on 11/24/15.
 */
public class BootStrapServer {
    HashMap<Integer,Integer> clients;
    HashMap<Integer,Integer> ports;
    ObjectOutputStream out;
    ObjectInputStream in;
    int currentClient;

    public static void main(String args[]){
        BootStrapServer server = new BootStrapServer();
        int peer = 1;
        server.clients = new HashMap<>();
        server.ports = new HashMap<>();
        server.currentClient = 1;

        while(peer < 6){
            server.clients.put(peer,-1);
            peer++;
        }

        server.run();
    }

    void run(){
        try {
            // Get the Socket from Config File
            ServerSocket bootStrap = new ServerSocket(4100);
            System.out.println("BootStrap Server Started");

            // Keep Listening
            boolean keepRunning = true;
            int firstPort = 0;
            while (keepRunning){
                // Try to Accept a Connection
                try{
                    Socket client = bootStrap.accept();
                    System.out.println("Connection Accepted from Client");

                    try {
                        // Receive the Client Listening Port from the Peer
                        out = new ObjectOutputStream(client.getOutputStream());
                        out.flush();
                        in = new ObjectInputStream(client.getInputStream());

                        //Try to receive a Connection from the Client
                        int port;
                        try {
                            port = in.readInt();
                            System.out.println("Client Successfully Read  --->  " + port);

                            // Add this Port to the List of Ports
                            if (!ports.containsKey(port)) {
                                int clientNo = currentClient;
                                System.out.println(currentClient);
                                ports.put(port, clientNo);

                                if(currentClient == 1){
                                    firstPort = port;
                                }

                                // Add this to the Circle as Well
                                if (clients.containsKey(clientNo - 1)) {
                                    clients.put(clientNo - 1, port);
                                }

                                currentClient++;
                            }
                            // Complete the Circle - Can Get the Max Number of Peers in our Circle from the User
                            if (currentClient == 6) {
                                clients.put(5,firstPort);
                            }

                            // Get the Client Number for the Port
                            int clientNo = ports.get(port);
                            int portNumber = -1;
                            if(clients.containsKey(clientNo)) {
                                portNumber = clients.get(clientNo);
                            }

                            // Send the Port Number to the Client
                            try{
                                out.writeInt(portNumber);
                                out.flush();
                            }catch (IOException e){
                                System.out.println("Cannot Send Port to the Number");
                            }

                            System.out.println(portNumber);

                        } catch (IOException e) {
                            System.out.println("Cannot Read the Port Number of the Client");
                        }
                    }catch (IOException e){
                        System.out.println("Cannot Initiate the Streams");
                    }
                } catch (IOException e){
                    System.out.println("Cannot Accept Connection From the Client");
                }
            }
        }catch (IOException e){
            System.out.println("Cannot Start Server");
        }
    }
}
