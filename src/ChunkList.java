import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by pranav on 11/22/15.
 */
public class ChunkList implements Serializable{
    HashSet<Chunk> chunks;

    public ChunkList(HashSet<Chunk> chunks) {
        this.chunks = chunks;
    }
}
