package concurrency;

import functions.Function0;
import functions.Function1;
import org.junit.Test;
import tuples.Tuple0;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;

/**
 * User: msfroh
 * Date: 2012-12-26
 * Time: 1:00 AM
 */
public class AsyncFunctionTest {

  @Test
  public void testChainedCalls() throws Exception {
    // Core pieces: a value-holder that returns zero, and a function that
    // adds 1 to its input.
    final Function0<Integer> zero = new Function0<Integer>() {
      @Override
      public Integer evaluate() {
        return 0;
      }
    };
    final Function1<Integer, Integer> addOne = new Function1<Integer, Integer>() {
      @Override
      public Integer evaluate(final Function1<Integer, Integer> self, final Integer i1) {
        return i1 + 1;
      }
    };
    final ExecutorService executorService =
      Executors.newSingleThreadExecutor();

    // Wrap our "zero" value in an evaluator so we can "evaluate" it
    // asynchronously.
    final AsyncEvaluator<Integer> zeroEvaluator =
      new AsyncEvaluator<>(zero, executorService);

    // Chain 10 calls to addOne, so they execute asynchronously as soon as the
    // previous one completes.
    AsyncEvaluator<Integer> previousEvaluator = zeroEvaluator;
    Function0<Integer> v = zero;
    for (int i = 0; i < 10; i++) {
      v = addOne.apply(v);
      previousEvaluator = previousEvaluator.chain(v);
    }

    // Create a place to keep our final answer, and add a callback to the last
    // value to add the answer to the queue.
    final BlockingQueue<Integer> answerQueue = new LinkedBlockingQueue<>();
    previousEvaluator.addCallback(new Function1<Tuple0, Integer>() {

      @Override
      public Tuple0 evaluate(final Function1<Tuple0, Integer> self,
                             final Integer i1) {
        answerQueue.add(i1);
        return Tuple0.INSTANCE;
      }
    });

    // Finally kick off the chained execution and wait for the result to
    // show up in answerQueue.
    zeroEvaluator.invoke();
    assertEquals(10, answerQueue.take().intValue());
  }
}
