package concurrency;

import collections.ImmutableList;
import functions.Function0;
import functions.Function1;
import functions.Function2;
import org.junit.Test;
import tuples.Tuple0;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;
import static utils.ListUtils.foldLeft;

/**
 * User: msfroh
 * Date: 2012-12-28
 * Time: 11:55 PM
 */
public class ConcurrentEvaluatorTest {
  private static final String url1 =
    "http://mikefroh.blogspot.com/2012/11/what-is-functional.html";
  private static final String url2 =
    "http://mikefroh.blogspot.com/2012/07/why-i-like-scala.html";

  @Test
  public void testSerialPageDownload() throws Exception {
    final long start = System.currentTimeMillis();
    int byteCount = addByteCounts(getPageBytes(url1), getPageBytes(url2)).get();
    final long time = System.currentTimeMillis() - start;
    System.out.println("testSerialPageDownload returned " + byteCount +
      " after " + time + "ms");
  }

  @Test
  public void testConcurrentPageDownload() throws Exception {
    ExecutorService executorService =
      Executors.newFixedThreadPool(2);
    ConcurrentEvaluator evaluator = new ConcurrentEvaluator(executorService);
    final long start = System.currentTimeMillis();
    int byteCount = addByteCounts(evaluator.evaluateConcurrent(getPageBytes(url1)),
      evaluator.evaluateConcurrent(getPageBytes(url2))).get();
    final long time = System.currentTimeMillis() - start;
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    System.out.println("testConcurrentPageDownload returned " + byteCount +
      " after " + time + "ms");
  }

  @Test
  public void testAsyncPageDownload() throws Exception {
    ExecutorService executorService =
      Executors.newFixedThreadPool(2);

    // Set up the lazy evaluation structure leading to our answer
    // If we call .get() on sumOfPageBytes, we get the same answer, just
    // evaluated synchronously on a single thread.
    Function0<Iterable<? extends Byte>> page1Bytes = getPageBytes(url1);
    Function0<Iterable<? extends Byte>> page2Bytes = getPageBytes(url2);
    Function0<Integer> sumOfPageBytes = addByteCounts(page1Bytes, page2Bytes);

    // Create evaluators for the two page loads
    AsyncEvaluator<?> page1Evaluator = new AsyncEvaluator<>(page1Bytes,
      executorService);
    AsyncEvaluator<?> page2Evaluator = new AsyncEvaluator<>(page2Bytes,
      executorService);

    // We'll need somewhere to store our answer. In a real program, we would
    // ideally avoid blocking by simply sending our answer across the network
    // or outputting it to the user.
    final BlockingQueue<Integer> answerQueue = new LinkedBlockingQueue<>();

    // Trigger evaluation of the answer once the two page loads are done and add
    // a callback to put its value in the answerQueue.
    AsyncEvaluator.joinedChain(sumOfPageBytes, executorService, page1Evaluator,
      page2Evaluator)
        .addCallback(new Function1<Tuple0, Integer>() {
          @Override
          public Tuple0 evaluate(final Function1<Tuple0, Integer> self,
                                 final Integer i1) {
            answerQueue.add(i1);
            return Tuple0.INSTANCE;
          }
        });

    // Kick off the two page loads
    final long start = System.currentTimeMillis();
    page1Evaluator.invoke();
    page2Evaluator.invoke();

    // Block until our answerQueue gets a value
    Integer byteCount = answerQueue.poll(5, TimeUnit.SECONDS);
    final long time = System.currentTimeMillis() - start;
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    System.out.println("testAsyncPageDownload returned " + byteCount +
      " after " + time + "ms");

    assertTrue(byteCount > 100000);
  }

  // Returns a Function0 that, when evaluated, will compute the length of each
  // pageByte iterable and then adds them together.
  private static Function0<Integer>
      addByteCounts(Function0<Iterable<? extends Byte>> pageBytes1,
                    Function0<Iterable<? extends Byte>> pageBytes2) {

    Function2<Integer, Integer, Byte> accumulateByteCount =
      new Function2<Integer, Integer, Byte>() {
        @Override
        public Integer evaluate(final Function2<Integer, Integer, Byte> self,
                                final Integer i1, final Byte i2) {
          return i1 + 1;
        }
      };
    Function2<Integer, Integer, Integer> addInts =
      new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer evaluate(final Function2<Integer, Integer, Integer> self,
                                final Integer i1, final Integer i2) {
          return i1 + i2;
        }
      };

    return addInts.apply(foldLeft(accumulateByteCount).apply(0).apply(pageBytes1))
      .apply(foldLeft(accumulateByteCount).apply(0).apply(pageBytes2));
  }

  // Downloads the resource from the given HTTP url and returns it as an ImmutableList
  // of bytes.
  private static Function0<Iterable<? extends Byte>> getPageBytes(final String url) {
    return new Function0<Iterable<? extends Byte>>() {
      @Override
      public Iterable<Byte> evaluate() {
        try {
          HttpURLConnection httpURLConnection =
            (HttpURLConnection) new URL(url).openConnection();
          ImmutableList<Byte> pageBytesReversed = ImmutableList.nil();
          try (BufferedInputStream bufferedStream =
                 new BufferedInputStream(httpURLConnection.getInputStream())) {
            byte pageByte;
            while ((pageByte = (byte) bufferedStream.read()) != -1) {
              pageBytesReversed = pageBytesReversed.prepend(pageByte);
            }
          }
          return pageBytesReversed.reverse();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }
}