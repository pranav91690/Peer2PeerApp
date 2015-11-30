import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by pranav on 11/22/15.
 */
public class SummaryList implements Serializable
{
    HashSet<Integer> chunkIDs;
    public SummaryList(HashSet<Integer> chunkIDs)
    {
        this.chunkIDs = chunkIDs;
    }
}
