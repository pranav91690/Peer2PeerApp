import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by pranav on 11/17/15.
 */
public class FileOwnerToPeer implements Serializable {
    int numberOfChunks;
    ArrayList<Chunk> chunks;
    String fileType;

    public FileOwnerToPeer(int numberOfChunks, ArrayList<Chunk> chunks, String fileType) {
        this.numberOfChunks = numberOfChunks;
        this.chunks = chunks;
        this.fileType = fileType;
    }
}
