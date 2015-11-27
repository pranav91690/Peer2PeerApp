import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by pranav on 11/18/15.
 */

// This is the thread that listens to the Upload Neighbour
public class ListenForUpload implements Runnable {
    int port;
    ObjectInputStream in;
    ObjectOutputStream out;
    HashMap<Integer,Chunk> chunks;
    HashSet<Integer> chunkIDs;

    public ListenForUpload(int port,HashMap<Integer,Chunk> chunks, HashSet<Integer> chunkIDs) {
        this.port = port;
        this.chunks = chunks;
        this.chunkIDs = chunkIDs;
    }


    public void run(){
        // Create a New Socket
        ServerSocket peerServer = null;
        try {
            peerServer = new ServerSocket(port);
            System.out.println("Client Started Listening on Port" + port);
            try {
                Socket uploadNeighbour = peerServer.accept();
                System.out.println("Client Accepted Upload Neighbour Request");
                try{
                    out = new ObjectOutputStream(uploadNeighbour.getOutputStream());
                    in = new ObjectInputStream(uploadNeighbour.getInputStream());

                    // Receive Requests and Send Replies Back
                    Object resp = null;
                    boolean connectionAlive = true;
                    while(connectionAlive) {
                        try {
                            resp = in.readObject();
                            if (resp instanceof String) {
                                System.out.println("<--- Rvd Req for Summary List");
                                // Send Summary List
                                try {
                                    SummaryList slist = new SummaryList(chunkIDs);
                                    out.writeObject(slist);
                                    out.flush();
//                                    System.out.println("---> Sent Summary List");
                                } catch (IOException e) {
                                    System.out.println("Cannot Write Summary List");
                                }
                            } else if (resp instanceof SummaryList) {
//                                System.out.println("<--- Rvd Req for Missing Chunks");
                                HashSet<Chunk> reqChunks = new HashSet<>();
                                // Send List of Requested Objects
                                for (Integer i : ((SummaryList) resp).chunkIDs) {
                                    reqChunks.add(chunks.get(i));
//                                    System.out.println(chunks.get(i).chunkID);
                                }
                                try {
                                    out.writeObject(new ChunkList(reqChunks));
                                    out.flush();
//                                    System.out.println(reqChunks);
//                                    System.out.println("---> Sent Missing Chunks");
                                } catch (IOException e) {
                                    System.out.println("Cannot Write to Stream");
                                }
                            }
                        } catch (IOException e) {
                            System.out.println(e);
                            break;
                        } catch (ClassNotFoundException e) {
                            System.out.println("Cannot Recognize Object");
                            break;
                        }
                    }
                }catch (IOException e){
                    System.out.println("Cannot Open Connection with the Stream");
                }
            }catch (IOException e) {
                System.out.println("Cannot Accept Connection From the Peer");
            }
        }catch (IOException e){
            System.out.println("Cannot Create a Peer Socket");
        }
    }
}
