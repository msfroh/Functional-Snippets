package functions;

import java.io.PrintStream;

/**
 * User: msfroh
 * Date: 12-06-11
 * Time: 12:42 AM
 */
public class LoggingFunction1<R, T1> extends Function1<R, T1>{
    private final Function1<R, T1> wrappedFunction;
    private final PrintStream logStream;
    private final String name;

    private LoggingFunction1(final Function1<R, T1> wrappedFunction,
                            final PrintStream logStream, final String name) {
        this.wrappedFunction = wrappedFunction;
        this.logStream = logStream;
        this.name = name;
    }

    @Override
    public R evaluate(final Function1<R, T1> self, final T1 i1) {
        logStream.println(name + " called with " + i1);
        final R returnValue = wrappedFunction.evaluate(wrappedFunction, i1);
        logStream.println(name + " returned " + returnValue);
        return returnValue;
    }

    public static <R, T1> Function1<R, T1>
        addLogging(final Function1<R, T1> wrappedFunction,
                   final PrintStream logStream, final String name) {
        return new LoggingFunction1<R, T1>(wrappedFunction, logStream, name);
    }
}
