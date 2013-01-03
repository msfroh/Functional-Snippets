package concurrency;

import functions.Function1;
import tuples.Tuple0;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * User: msfroh
 * Date: 2012-12-21
 * Time: 3:03 PM
 */
public class AsyncTask<V> extends FutureTask<V> {

    private final Function1<Tuple0, Future<V>> callback;

    public AsyncTask(final Callable<V> callable,
                     Function1<Tuple0, Future<V>> callback) {
        super(callable);
        this.callback = callback;
    }

    @Override
    protected void done() {
        callback.apply(this).evaluate();
    }
}
