import java.io.File;
import java.io.Serializable;

/**
 * Created by pranav on 11/17/15.
 */
public class Chunk implements Serializable {
    int chunkID;
    byte[] bytes;

    public Chunk(int chunkID, byte[] bytes) {
        this.chunkID = chunkID;
        this.bytes = bytes;
    }
}
