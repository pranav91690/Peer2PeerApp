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
        // Keep Connecting to the Download Neighbour for Data
        boolean incomplete = true;
        while(incomplete) {
            // Wait Some Time Before Trying to Connect Again
            try{
                Thread.sleep(5000);
            }catch (InterruptedException e){
                System.out.println("Some Problem with the Thread");
            }
            // Connect Each Time to the Download Neighbour Port
            boolean dataTransferred = false;
            int state = 0;

            try {
                // Open a Connection to the Download Neighbour
                Socket downloadNeighbourSocket = new Socket("localhost", downloadNeighbourPort);
                System.out.println("---> Client Connected to Download Neighbour " + downloadNeighbourPort);

                // Initiate the Input and Output Buffer Streams for the Socket
                out = new ObjectOutputStream(downloadNeighbourSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(downloadNeighbourSocket.getInputStream());

                while(!dataTransferred) {
                    // Receive the Summary List//Chunks ID's
                    Object resp = null;
                    HashSet<Integer> rvdIDs = null;
                    resp = in.readObject();
                    if (state == 0 && resp instanceof SummaryList) {
                        rvdIDs = ((SummaryList) resp).chunkIDs;
                        System.out.println("Client Rvd Summary List <--- " + rvdIDs);
                        // Send Required Chunks Back
                        if (rvdIDs != null) {
                            // Get the Missing Chunks
                            rvdIDs.removeAll(chunkIDs);
                            if (!rvdIDs.isEmpty()) {
                                // Send a Request to the Download Peer
                                //System.out.println(rvdIDs);
                                SummaryList wantedIDs = new SummaryList(rvdIDs);
                                try {
                                    out.writeObject(wantedIDs);
                                    out.flush();
                                    System.out.println(rvdIDs + " Client ---> Sent Req for Required ID's");
                                    //System.out.println("--->" + rvdIDs);
                                } catch (IOException e) {
                                    System.out.println("Cannot Send ID Request");
                                }
                            }
                        }
                        state = 1;
                    } else if (state == 1 && resp instanceof ChunkList) {
                        System.out.print("Client Rvd Wanted IDs <--- ");
                        // Add to the Summary List
                        for (Chunk c : ((ChunkList) resp).chunks) {
                            chunkIDs.add(c.chunkID);
                            chunks.put(c.chunkID, c);
                            rvdChunks++;
                            System.out.print(c.chunkID + " ");
                        }
                        System.out.println();
                        System.out.println("Chunk List" + chunkIDs);

                        dataTransferred = true;

                        // Close the Connection Now
                        in.close();
                        out.close();
                        downloadNeighbourSocket.close();
                    }
                }
            } catch (IOException e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    System.out.println("Cannot Pause the Thread");
                }
            } catch (ClassNotFoundException c) {
                System.out.println(c);
            }

            // Check if all Chunks Received
            if (rvdChunks == numberOfChunks) {
                incomplete = false;
                System.out.println(chunkIDs);
                try {
                    mergeFiles();
                } catch (IOException e) {
                    System.out.println(e);
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
