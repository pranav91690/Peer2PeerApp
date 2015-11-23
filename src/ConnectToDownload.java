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
                System.out.println("Client Connected to Download Neighbour");
                connected = true;
                // Open a Connection to the Download Neighbour
                Socket downloadNeighbourSocket = new Socket("localhost", downloadNeighbourPort);

                // Initiate the Input and Output Buffer Streams for the Socket
                try {
                    out = new ObjectOutputStream(downloadNeighbourSocket.getOutputStream());
                    in = new ObjectInputStream(downloadNeighbourSocket.getInputStream());

                    // Request for it's Client ID List
                    // Get the Output Stream of the Download Neighbour
                    // Repeat this Every 100ms
                    boolean keepRunning = true;

                    while (keepRunning) {
                        // Check if Number of
                        if (rvdChunks == numberOfChunks) {
                            keepRunning = false;
                            // Merge the Files
                            mergeFiles();
                        }
                        try {
                            out.writeObject("SendSummaryList");
                            out.flush();
                            System.out.println("Sent Req for Summary List");

                            SummaryList resp = null;

                            // Get the Response Now
                            try {
                                // Get the Summary List Object
                                resp = (SummaryList) in.readObject();
                                System.out.println("Received Summary List");

                                // Compare with the Master List
                                resp.chunkIDs.removeAll(chunkIDs);

                                // Request the Remaining Id's
                                if (!resp.chunkIDs.isEmpty()) {
                                    // Send a Request to the Download Peer
                                    SummaryList wantedIDs = new SummaryList(resp.chunkIDs);
                                    try {
                                        out.writeObject(wantedIDs);
                                        out.flush();
                                        System.out.println("Sent Wanted IDs");
                                        ChunkList chunkList = null;

                                        // Get the Requested ID's Data
                                        try {
                                            chunkList = (ChunkList) in.readObject();
                                            System.out.println("Received Wanted IDs");
                                            // Add to the Summary List
                                            for (Chunk c : chunkList.chunks) {
                                                chunkIDs.add(c.chunkID);
                                                chunks.put(c.chunkID, c);
                                                rvdChunks++;
                                            }
                                        } catch (ClassNotFoundException e) {
                                            System.out.println("Cannot Read the Requested ID's");
                                        } catch (IOException e) {
                                            System.out.println("Cannot Connect to the Input Stream");
                                        }
                                    } catch (IOException e) {
                                        System.out.println("Cannot send Wanted ID's request");
                                    }
                                }
                            } catch (ClassNotFoundException e) {
                                System.out.println("Unrecognized Class in the Object");
                            } catch (IOException e) {
                                System.out.println("Cannot Read Summary List");
                            }
                        } catch (IOException e) {
                            System.out.println("Cannot connect to the Output Stream");
                        }
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            System.out.println("Failed to Sleep");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Cannot Connect to the In/Out Streams");
                }
            } catch (IOException e) {
                System.out.println("Waiting To Connect");
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
