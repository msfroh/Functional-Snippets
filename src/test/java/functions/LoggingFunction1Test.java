package functions;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static functions.LoggingFunction1.addLogging;
import static org.junit.Assert.assertEquals;
import static tuples.TupleUtils.tupled;
import static tuples.TupleUtils.untupled2;

/**
 * User: msfroh
 * Date: 12-06-11
 * Time: 12:49 AM
 */
public class LoggingFunction1Test {

    @Test
    public void testFunction1() throws Exception {
        Function1<Integer, String> getLength =
                new Function1<Integer, String>() {
            @Override
            public Integer evaluate(final Function1<Integer, String> self,
                                    final String i1) {
                return i1.length();
            }
        };
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(bytes);
        Function1<Integer, String> loggingGetLength =
                addLogging(getLength, outputStream, "getLength");
        loggingGetLength.apply("hello").get();
        assertEquals("getLength called with hello\n" +
                "getLength returned 5\n",
                new String(bytes.toByteArray()));
    }

    @Test
    public void testFunction2() throws Exception {
        Function2<Integer, Integer, Integer> add =
                new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer evaluate(
                    final Function2<Integer, Integer, Integer> self,
                    final Integer i1, final Integer i2) {
                return i1 + i2;
            }
        };
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(bytes);
        Function2<Integer, Integer, Integer> loggingAdd =
                untupled2(addLogging(tupled(add), outputStream, "add"));
        loggingAdd.apply(4, 5).get();
        assertEquals("add called with (4,5)\n" +
                "add returned 9\n",
                new String(bytes.toByteArray()));
    }


}
