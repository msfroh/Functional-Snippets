package collections;

import functions.Function1;
import functions.Function2;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static collections.Option.*;
import static org.junit.Assert.*;

/**
 * User: froh
 * Date: 4/5/12
 * Time: 12:39 PM
 */
public class OptionTest {
    @Test
    public void testGet() throws Exception {
        Option<Integer> a = some(5);
        Option<Integer> b = none();

        assertEquals(5, a.get().intValue());
        try {
            b.get();
            fail("Should have thrown exception");
        } catch (NoSuchElementException e) {
            // Exception should have been thrown
        }

        // Some should return its own value
        assertEquals(5, a.getOrElse(1).intValue());

        // None returns the given default value
        assertEquals(1, b.getOrElse(1).intValue());
    }


    @Test
    public void testMap() throws Exception {
        Option<Integer> a = some(5);
        Option<Integer> b = none();
        Function1<Integer, Integer> dbl = new Function1<Integer, Integer>() {
            @Override
            public Integer evaluate(final Function1<Integer, Integer> self,
                                    final Integer i1) {
                return i1 * 2;
            }
        };
        assertEquals(some(10), a.map(dbl));
        assertEquals(Option.<Integer>none(), b.map(dbl));
    }

    @Test
    public void testFold() throws Exception {
        Function2<String, String, String> concat =
                new Function2<String, String, String>() {
                    @Override
                    public String evaluate(final Function2<String, String, String> self,
                                           final String i1, final String i2) {
                        return i1 + i2;
                    }
                };

        Option<String> a = some("a");
        assertEquals("ba", a.foldLeft(concat, "b"));
        assertEquals("ab", a.foldRight(concat, "b"));

        Option<String> b = none();
        assertEquals("b", b.foldLeft(concat, "b"));
        assertEquals("b", b.foldRight(concat, "b"));
    }

    @Test
    public void testFlatMap() throws Exception {
        Option<Integer> a = some(5);
        Option<Integer> b = none();
        Option<Integer> c = some(0);
        // safeDivide prevents divByZero errors by returning None
        Function2<Option<Integer>, Integer, Integer> safeDivide =
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
        assertEquals(some(2), a.flatMap(safeDivide.apply(10)));
        // 10 / none returns none
        assertEquals(Option.<Integer>none(), b.flatMap(safeDivide.apply(10)));
        // 10 / 0 returns none
        assertEquals(Option.<Integer>none(), c.flatMap(safeDivide.apply(10)));
    }

    @Test
    public void testFilter() throws Exception {
        Option<Integer> a = some(2);
        Option<Integer> b = some(1);
        Option<Integer> c = none();

        Function1<Boolean, Integer> isEven = new Function1<Boolean, Integer>() {
            @Override
            public Boolean evaluate(final Function1<Boolean, Integer> self,
                                    final Integer i1) {
                return i1 % 2 == 0;
            }
        };

        // a is defined and even
        assertEquals(some(2), a.filter(isEven));
        // b is defined, but not even
        assertEquals(Option.<Integer>none(), b.filter(isEven));
        // c is undefined
        assertEquals(Option.<Integer>none(), c.filter(isEven));
    }

    @Test
    public void testIterable() throws Exception {
        // For Some, the body of the for loop will execute
        Option<Integer> a = some(5);
        boolean didRun = false;
        for (Integer i : a) {
            didRun = true;
            assertEquals(Integer.valueOf(5), i);
        }
        assertTrue(didRun);

        // For None, it does not execute
        Option<Integer> b = none();
        for (Integer i : b) {
            fail("This should not execute");
        }
    }

    @Test
    public void testWrapNull() throws Exception {
        Map<String, Integer> scoreMap = new HashMap<String, Integer>();
        scoreMap.put("Michael", 42);

        Option<Integer> a = option(scoreMap.get("Michael"));
        assertTrue(a.isDefined());

        Option<Integer> b = option(scoreMap.get("Bob"));
        assertFalse(b.isDefined());

        // Here is the ugly "traditional" Java way of dealing with Maps
        Integer score = scoreMap.get("Michael");
        if (score != null) {
            System.out.println("Michael's score is " + score);
        }

        // This feels more elegant to me
        for (Integer myScore : option(scoreMap.get("Michael"))) {
            System.out.println("Michael's score is " + myScore);
        }
    }
}
