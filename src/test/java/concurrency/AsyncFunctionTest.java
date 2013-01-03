package concurrency;

import functions.Function0;
import functions.Function1;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

/**
 * User: msfroh
 * Date: 2012-12-26
 * Time: 1:00 AM
 */
public class AsyncFunctionTest {

    @Test
    public void testChainedCalls() throws Exception {
        Function0<Integer> zero = new Function0<Integer>() {
            @Override
            public Integer evaluate() {
                return 0;
            }
        };

        Function1<Integer, Integer> addOne = new Function1<Integer, Integer>() {
            @Override
            public Integer evaluate(final Function1<Integer, Integer> self, final Integer i1) {
                return i1 + 1;
            }
        };
        final ExecutorService executorService =
                Executors.newSingleThreadExecutor();

        final AsyncFunction0<Integer> asyncZero = new AsyncFunction0<>(zero, executorService);
        AsyncFunction0<Integer> av = asyncZero;
        for (int i = 0; i < 10; i++) {
            final AsyncFunction0<Integer> nextAsyncVal =
                    new AsyncFunction0<>(addOne.apply(av), executorService);
            av.chain(nextAsyncVal);
            av = nextAsyncVal;
        }
        asyncZero.invokeAsync();
        assertEquals(10, av.get().intValue());
    }
}
