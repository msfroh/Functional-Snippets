package collections;


import functions.Function1;
import functions.Function2;
import org.junit.Test;

import static collections.ImmutableList.list;
import static org.junit.Assert.assertEquals;

public class ImmutableListTest {

    @Test
    public void testListIteration() {
        ImmutableList<Integer> list = list(1, 2, 3);
        int i = 1;
        for (Integer l : list) {
            assertEquals(i++, l.intValue());
        }
    }

    @Test
    public void testMap() throws Exception {
        ImmutableList<Integer> list = list(1, 2, 3);
        Function1<Integer, Integer> dbl = new Function1<Integer, Integer>() {
            @Override
            public Integer evaluate(final Function1<Integer, Integer> self,
                                    final Integer i1) {
                return i1 * 2;
            }
        };
        ImmutableList<Integer> doubledList = list.map(dbl);
        assertEquals(list(2, 4, 6), doubledList);
    }

    @Test
    public void testFold() {
        Function2<String, String, String> concat =
                new Function2<String, String, String>() {
                    @Override
                    public String evaluate(
                            final Function2<String, String, String> self,
                            final String i1, final String i2) {
                        return i1 + i2;
                    }
                };

        ImmutableList<String> list = list("a", "b", "c");
        assertEquals("dabc", list.foldLeft(concat, "d"));
        assertEquals("abcd", list.foldRight(concat, "d"));

        ImmutableList<String> emptyList = list();
        assertEquals("d", emptyList.foldLeft(concat, "d"));
        assertEquals("d", emptyList.foldRight(concat, "d"));
    }

    @Test
    public void testFlatMap() throws Exception {
        ImmutableList<Integer> list = list(1, 2, 3);

        // oneToN :: n -> [1..n]
        Function1<ImmutableList<Integer>, Integer> oneToN =
                new Function1<ImmutableList<Integer>, Integer>() {
                    @Override
                    public ImmutableList<Integer>
                    evaluate(final Function1<ImmutableList<Integer>, Integer> self,
                             final Integer i1) {
                        ImmutableList<Integer> list = ImmutableList.nil();
                        for (int i = i1; i >= 1; i--) {
                            list = list.prepend(i);
                        }
                        return list;
                    }
                };
        assertEquals(list(1, 1, 2, 1, 2, 3), list.flatMap(oneToN));
    }

    @Test
    public void testFilter() throws Exception {
        ImmutableList<Integer> list = list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Function1<Boolean, Integer> isEven = new Function1<Boolean, Integer>() {
            @Override
            public Boolean evaluate(final Function1<Boolean, Integer> self,
                                    final Integer i1) {
                return i1 % 2 == 0;
            }
        };
        
        assertEquals(list(2, 4, 6, 8, 10), list.filter(isEven));
    }

    @Test
    public void testSize() throws Exception {
        int size = 100000;
        Byte[] bytes = new Byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (i % 256);
        }
        ImmutableList<Byte> byteList = list(bytes);
        assertEquals(size, byteList.size());
    }
}
