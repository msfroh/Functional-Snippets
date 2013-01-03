package concurrency;

import collections.ImmutableList;
import functions.Function0;
import functions.Function2;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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