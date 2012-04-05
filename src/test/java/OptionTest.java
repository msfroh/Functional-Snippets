import collections.Option;
import functions.Function1;
import functions.Function2;
import org.junit.Test;

import static collections.Option.none;
import static collections.Option.some;
import static org.junit.Assert.assertEquals;

/**
 * User: froh
 * Date: 4/5/12
 * Time: 12:39 PM
 */
public class OptionTest {
    @Test
    public void testMap() throws Exception {
        Option<Integer> a = some(5);
        Option<Integer> b = none();
        Function1<Integer, Integer> dbl = new Function1<Integer, Integer>() {
            @Override
            public Integer evaluate(final Function1<Integer, Integer> self,
                                    final Integer i1) {
                return i1  *2;
            }
        };
        assertEquals(some(10), a.map(dbl));
        assertEquals(Option.<Integer>none(), b.map(dbl));
    }

    @Test
    public void testFold() throws Exception {
        Option<String> a = some("a");
        Option<String> b = none();
        Function2<String, String, String> concat =
                new Function2<String, String, String>() {
            @Override
            public String evaluate(final Function2<String, String, String> self,
                                   final String i1, final String i2) {
                return i1 + i2;
            }
        };
        assertEquals("ba", a.foldLeft(concat, "b"));
        assertEquals("b", b.foldLeft(concat, "b"));
        assertEquals("ab", a.foldRight(concat, "b"));
        assertEquals("b", b.foldRight(concat, "b"));
    }

    @Test
    public void testFlatMap() throws Exception {
        Option<Integer> a = some(5);
        Option<Integer> b = none();
        Option<Integer> c = some(0);
        Function2<Option<Integer>, Integer, Integer> divide =
                new Function2<Option<Integer>, Integer, Integer>() {
                    @Override
                    public Option<Integer>
                    evaluate(final Function2<Option<Integer>, Integer, Integer> self,
                             final Integer i1, final Integer i2) {
                        if (i2 == 0) {
                            return none();
                        }
                        return some(i1 / i2);
                    }
                };
        // 10 / 5 returns 2
        assertEquals(some(2), a.flatMap(divide.apply(10)));
        // 10 / none returns none
        assertEquals(Option.<Integer>none(), b.flatMap(divide.apply(10)));
        // 10 / 0 returns none
        assertEquals(Option.<Integer>none(), c.flatMap(divide.apply(10)));
    }

    @Test
    public void testFilter() throws Exception {
        Option<Integer> a = some(5);
        Option<Integer> b = none();
        Option<Integer> c = some(1);
        Function2<Boolean, Integer, Integer> firstIsGreater =
                new Function2<Boolean, Integer, Integer>() {
                    @Override
                    public Boolean
                    evaluate(final Function2<Boolean, Integer, Integer> self,
                             final Integer i1, final Integer i2) {
                        return i1 > i2;
                    }
                };
        Function1<Boolean, Integer> lessThanFour = firstIsGreater.apply(4);
        // 4 > 5 returns none
        assertEquals(Option.<Integer>none(), a.filter(lessThanFour));
        // 4 > none returns none
        assertEquals(Option.<Integer>none(), b.filter(lessThanFour));
        // 4 > 1 returns 1
        assertEquals(some(1), c.filter(lessThanFour));
    }
}
