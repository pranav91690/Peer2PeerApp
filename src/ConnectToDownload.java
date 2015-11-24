import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by pranav on 11/18/15.
 */
public class ConnectToDownload implements Runnable {
    int downloadNeighbourPort;
    HashMap<Integer,Chunk> chunks;
    HashSet<Integer> chunkIDs;
    ObjectInputStream in;
    ObjectOutputStream out;
    int numberOfChunks;
    int clientPort;
    String fileType;
    int rvdChunks;

    // Constructor
    public ConnectToDownload(int downloadNeighbourPort,HashMap<Integer,Chunk> chunks,HashSet<Integer> chunkIDs,
                             int numberOfChunks, int clientPort,
                             String fileType, int rvdChunks)
    {
        this.downloadNeighbourPort = downloadNeighbourPort;
        this.chunks = chunks;
        this.chunkIDs = chunkIDs;
        this.numberOfChunks = numberOfChunks;
        this.clientPort = clientPort;
        this.fileType = fileType;
        this.rvdChunks = rvdChunks;
    }

    public void run(){
        boolean connected = false;
        while(!connected) {
            try {
                // Open a Connection to the Download Neighbour
                Socket downloadNeighbourSocket = new Socket("localhost", downloadNeighbourPort);
                connected = true;
                System.out.println("--->  Client Connected to Download Neighbour");
                // Initiate the Input and Output Buffer Streams for the Socket
                try {
                    out = new ObjectOutputStream(downloadNeighbourSocket.getOutputStream());
                    in = new ObjectInputStream(downloadNeighbourSocket.getInputStream());

                    Object resp = null;
                    HashSet<Integer> rvdIDs = null;
                    boolean keepRunning = true;
                    int stage = 0;
                    while (keepRunning) {
                        // Wait Some Time Before Sending the Next Request
                        try{
                            Thread.sleep(10000);
                        }catch (InterruptedException e){
                            System.out.println("Some Problem with the Thread");
                        }

                        // Print the Chunks Received
                        System.out.println(chunkIDs);

                        // Check if we received all Chunks
                        if (rvdChunks == numberOfChunks) {
                            keepRunning = false;
                            // Merge the Files
                            mergeFiles();
                        }

                        // If in stage 0, send req for SummaryList
                        if (stage == 0) {
                            try {
                                out.writeObject("SendSummaryList");
                                out.flush();
//                                System.out.println("---> Sent Req for Summary List");
                                stage = 1;
                            } catch (IOException e) {
                                System.out.println("Cannot Send Req for Summary List");
                            }
                        } else {
                            // Send Request for Missing Chunks
                            if (rvdIDs != null) {
                                // Get the Missing Chunks
                                rvdIDs.removeAll(chunkIDs);
                                if (!rvdIDs.isEmpty()) {
                                    // Send a Request to the Download Peer
//                                    System.out.println(rvdIDs);
                                    SummaryList wantedIDs = new SummaryList(rvdIDs);
                                    try {
                                        out.writeObject(wantedIDs);
                                        out.flush();
//                                        System.out.println("--->" + rvdIDs);
                                        stage = 0;
                                    } catch (IOException e) {
                                        System.out.println("Cannot Send ID Request");
                                    }
                                }
                            }
                        }

                        // Receive a Message From the Server
                        try {
                            resp = in.readObject();
                            if (resp instanceof SummaryList) {
//                                System.out.println("<--- Rvd Summary List");
                                rvdIDs = ((SummaryList) resp).chunkIDs;
                            } else if (resp instanceof ChunkList) {
//                                System.out.println("<--- Rvd Wanted IDs");
                                // Add to the Summary List
                                for (Chunk c : ((ChunkList) resp).chunks) {
                                    chunkIDs.add(c.chunkID);
                                    chunks.put(c.chunkID, c);
                                    rvdChunks++;
                                }
//                                System.out.println("<---" + ((ChunkList) resp).chunks);
                            }
                        } catch (IOException e) {
                            System.out.println("Cannot Read Message From the Server");
                        } catch (ClassNotFoundException e) {
                            System.out.println("No Such Class Exists");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Cannot Connect to the In/Out Streams");
                }
            } catch (IOException e) {
                try{
                    Thread.sleep(500);
                }catch (InterruptedException e1){
                    System.out.println("Cannot Pause the Thread");
                }
            }
        }
    }

    public void mergeFiles() throws IOException{
        // Create a New File Output Stream at this path
        FileOutputStream fos = new FileOutputStream(clientPort + "." +  fileType);
        try(BufferedOutputStream mergeStream = new BufferedOutputStream(fos)){
            for(Chunk c : chunks.values()){
                mergeStream.write(c.bytes);
            }
            // Close the Merge Stream
            mergeStream.close();
        }
    }
}
