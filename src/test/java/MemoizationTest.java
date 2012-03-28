import functions.Function1;
import functions.MemoizationUtils;
import functions.MemoizedFunction1;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class MemoizationTest  {
    private final Function1<Integer, Integer> fib =
            new Function1<Integer, Integer>() {
        @Override
        public Integer evaluate(final Function1<Integer, Integer> self,
                                final Integer i1) {
            if (i1 == 0 || i1 == 1) return 1;
            return self.apply(i1 - 1).get() + self.apply(i1 - 2).get();
        }
    };

    final Function1<Integer, Integer> memoFib =
            new MemoizedFunction1<Integer, Integer>() {
        @Override
        public Integer evaluate(final Function1<Integer, Integer> self,
                                final Integer i1) {
            if (i1 == 0 || i1 == 1) return 1;
            return apply(i1 - 1).get() + apply(i1 - 2).get();
        }
    };

    @Test
    public void testUnmemoizedFibonacci() {
        long start = System.currentTimeMillis();
        fib.apply(40).get();
        long end = System.currentTimeMillis();
        assertTrue(end - start > 2000);
        System.out.println("Unmemoized took " + (end - start));
    }

    @Test
    public void testMemoizedFibonacci() {
        long start = System.currentTimeMillis();
        memoFib.apply(40).get();
        long end = System.currentTimeMillis();
        assertTrue(end - start < 100);
        System.out.println("Memoized took " + (end - start));
    }

    @Test
    public void testRuntimeMemoization() {
        long start = System.currentTimeMillis();
        MemoizationUtils.memoize(fib).apply(40).get();
        long end = System.currentTimeMillis();
        assertTrue(end - start < 100);
    }
}
