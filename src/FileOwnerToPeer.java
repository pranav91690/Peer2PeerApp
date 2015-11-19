import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by pranav on 11/17/15.
 */
public class FileOwnerToPeer implements Serializable {
    int numberOfChunks;
    List<Chunk> chunks;
    String fileType;

    public FileOwnerToPeer(int numberOfChunks, List<Chunk> chunks, String fileType) {
        this.numberOfChunks = numberOfChunks;
        this.chunks = chunks;
        this.fileType = fileType;
    }
}
