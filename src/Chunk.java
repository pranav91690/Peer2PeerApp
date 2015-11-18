import java.io.File;

/**
 * Created by pranav on 11/17/15.
 */
public class Chunk {
    int chunkID;
    File file;

    public Chunk(int chunkID, File file) {
        this.chunkID = chunkID;
        this.file = file;
    }
}
