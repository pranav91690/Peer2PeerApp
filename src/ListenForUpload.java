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


    public void run() {
        ServerSocket peerServer = null;
        try {
            peerServer = new ServerSocket(port);
            System.out.println("---> Client Started Listening on Port " + port);
            while (true) {
                // Accept a new Connection
                try {
                    Socket uploadNeighbour = peerServer.accept();
                    System.out.println("Client Accepted Upload Neighbour Request");
                    // Receive Requests and Send Replies Back
                    Object resp = null;

                    out = new ObjectOutputStream(uploadNeighbour.getOutputStream());
                    out.flush();
                    in = new ObjectInputStream(uploadNeighbour.getInputStream());

                    // Send a Summary List
                    out.writeObject(new SummaryList(chunkIDs));
                    out.flush();
                    System.out.println("Server Sent Summary List ---> " + chunkIDs);

                    // Receive Wanted ID's
                    resp = in.readObject();
                    if (resp instanceof SummaryList) {
                        HashSet<Chunk> reqChunks = new HashSet<>();
                        // Send List of Requested Objects
                        System.out.print("Server Sent IDs ---> ");
                        for (Integer i : ((SummaryList) resp).chunkIDs) {
                            System.out.print(i + " ");
                            reqChunks.add(chunks.get(i));
                        }
                        System.out.println();
                        out.writeObject(new ChunkList(reqChunks));
                        out.flush();
                    }
                } catch (IOException e) {
                    System.out.println(e);
                } catch (ClassNotFoundException c) {
                    System.out.println(c);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
