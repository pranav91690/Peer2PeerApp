import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by pranav on 11/17/15.
 */
public class FileOwnerToPeer implements Serializable {
    // What are the Objects??? - Number of MasterList and Actual List of MasterList
    int numberOfChunks;
    List<Chunk> chunks;

    public FileOwnerToPeer(int numberOfChunks, List<Chunk> chunks) {
        this.numberOfChunks = numberOfChunks;
        this.chunks = chunks;
    }
}
