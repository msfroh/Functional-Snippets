package collections;

import org.junit.Test;
import tuples.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: msfroh
 * Date: 12-06-02
 * Time: 1:56 AM
 */
public class ImmutableHashTrieMapTest {
    private static final List<String> keys =
            Arrays.asList(
                    // The following values collide
                    "FB", "Ea",
                    // These are general pieces of data
                    "apple", "bacon", "cat", "dog",
                    "elephant", "funny", "giraffe", "hippopotamus", "ikea",
                    "jungle", "koala", "lemur", "math", "neptune", "oasis",
                    "purple", "quorum", "really", "sierra", "tuna", "umbrella",
                    "vacuum", "whiskey", "xylophone", "yellow", "zebra"
            );


    @Test
    public void testPutAndGet() throws Exception {
        ImmutableHashTrieMap<String, Integer> hash =
                ImmutableHashTrieMap.empty();
        for (String k : keys) {
            hash = hash.put(k, k.length());
        }
        for (String k : keys) {
            assertEquals(Option.some(k.length()), hash.get(k));
        }
    }

    @Test
    public void testRemoveEverything() throws Exception {
        ImmutableHashTrieMap<String, Integer> hash =
                ImmutableHashTrieMap.empty();
        for (String k : keys) {
            hash = hash.put(k, k.length());
        }
        Collections.shuffle(keys);
        for (String k : keys) {
            hash = hash.remove(k);
        }
        assertEquals(ImmutableHashTrieMap.<String, Integer>empty(), hash);
    }

    @Test
    public void testIteration() throws Exception {
        ImmutableHashTrieMap<String, Integer> hash =
                ImmutableHashTrieMap.empty();
        for (String k : keys) {
            hash = hash.put(k, k.length());
        }
        int numHits = 0;
        for (Tuple2<String, Integer> entry : hash) {
            System.out.println(entry._1 + " -> " + entry._2);
            numHits++;
        }
        assertEquals(keys.size(), numHits);
    }

    @Test
    public void testLargeRemoval() throws Exception {
        ImmutableHashTrieMap<Integer, Integer> hash =
                ImmutableHashTrieMap.empty();
        List<Integer> list = new ArrayList<Integer>(100000);
        for (int i = 0; i < 100000; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (Integer i : list) {
            hash = hash.put(i, i);
        }
        Collections.shuffle(list);
        for (Integer i : list) {
            hash = hash.remove(i);
        }
        assertEquals(ImmutableHashTrieMap.<Integer, Integer>empty(), hash);
    }
}
