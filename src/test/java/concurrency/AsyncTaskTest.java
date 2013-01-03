package concurrency;

import functions.Function1;
import org.junit.Test;
import tuples.Tuple0;

import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * User: msfroh
 * Date: 2012-12-21
 * Time: 3:32 PM
 */
public class AsyncTaskTest {

    @Test
    public void testAsyncExecute() throws Exception {
        final BlockingQueue<Future<Integer>> answerQueue =
                new LinkedBlockingQueue<>();

        Callable<Integer> asyncAddition = new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return 5 + 5;
            }
        };

        Function1<Tuple0, Future<Integer>> callback =
                new Function1<Tuple0, Future<Integer>>() {
            @Override
            public Tuple0 evaluate(final Function1<Tuple0, Future<Integer>> self,
                                   final Future<Integer> i1) {
                answerQueue.add(i1);
                return Tuple0.INSTANCE;
            }
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new AsyncTask<>(asyncAddition, callback));

        assertEquals(Integer.valueOf(10), answerQueue.take().get());

        // Asynchronous action that triggers an exception
        Callable<Integer> asyncDivideByZero = new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                int zero = 0;
                return 5 / zero;
            }
        };
        executorService.submit(new AsyncTask<>(asyncDivideByZero, callback));
        Future<Integer> answer = answerQueue.take();
        try {
            answer.get();
            fail("Exception should have been thrown");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof ArithmeticException);
        }

        executorService.shutdown();
    }

    @Test
    public void testChainedCalls() throws Exception {
        final BlockingQueue<Integer> answerQueue =
                        new LinkedBlockingQueue<>();
        Callable<Integer> one = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        };
        final ExecutorService executorService =
                Executors.newSingleThreadExecutor();
        final Function1<Integer, Integer> addOne =
                new Function1<Integer, Integer>() {

                    @Override
                    public Integer evaluate(final Function1<Integer, Integer> self,
                                            final Integer i1) {
                        return i1 + 1;
                    }
                };


        Function1<Tuple0, Future<Integer>> incrementToTen =
                new Function1<Tuple0, Future<Integer>>() {
            @Override
            public Tuple0 evaluate(final Function1<Tuple0, Future<Integer>> self,
                                   final Future<Integer> i1) {
                try {
                    if (i1.get() >= 10) {
                        answerQueue.add(i1.get());
                    } else {
                        executorService.submit(
                                new AsyncTask<>(addOne.apply(i1.get()), this)
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Tuple0.INSTANCE;
            }
        };

        executorService.submit(new AsyncTask<>(one, incrementToTen));

        assertEquals(10, answerQueue.poll(5, TimeUnit.SECONDS).intValue());
    }
}
