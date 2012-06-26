package tuples;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static tuples.TupleUtils.tuple;

/**
 * User: msfroh
 * Date: 12-06-09
 * Time: 1:13 AM
 */
public class Tuple2Test {
    private Tuple2<Integer, Integer> vectorAdd(Tuple2<Integer, Integer> v1,
                                               Tuple2<Integer, Integer> v2) {
        return tuple(v1._1 + v2._1, v1._2 + v2._2);
    }

    private String describePerson(Tuple2<String, Integer> nameAge) {
        return nameAge._1 + " is " + nameAge._2 + " years old";
    }

    @Test
    public void testTuple2() throws Exception {
        Tuple2<Integer, Integer> v1 = tuple(1, 1);
        Tuple2<Integer, Integer> v2 = tuple(3, 4);

        Tuple2<Integer, Integer> vsum = vectorAdd(v1, v2);

        assertEquals(Integer.valueOf(4), vsum._1);
        assertEquals(Integer.valueOf(5), vsum._2);

        assertEquals("Michael is 32 years old",
                describePerson(tuple("Michael", 32)));
    }
}
