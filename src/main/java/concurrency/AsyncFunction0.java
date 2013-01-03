package concurrency;

import functions.Function0;
import functions.Function1;
import tuples.Tuple0;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * User: msfroh
 * Date: 2012-12-24
 * Time: 5:27 PM
 */
public class AsyncFunction0<R> extends Function0<R> {
    private final Queue<Function1<Tuple0, Future<R>>> callbacks =
            new ConcurrentLinkedQueue<>();
    private final Function0<R> synchronousFunction0;
    private final ExecutorService executorService;

    protected AsyncFunction0(final Function0<R> synchronousFunction0,
                             final ExecutorService executorService) {
        this.synchronousFunction0 = synchronousFunction0;
        this.executorService = executorService;
    }

    public void addCallback(Function1<Tuple0, Future<R>> callback) {
        callbacks.add(callback);
    }

    public void invokeAsync() {
        executorService.submit(new AsyncTask<>(
                this,
                new Function1<Tuple0, Future<R>>() {
                    @Override
                    public Tuple0 evaluate(final Function1<Tuple0, Future<R>> self,
                                           final Future<R> i1) {
                        for (Function1<Tuple0, Future<R>> callback : callbacks) {
                            callback.evaluate(i1);
                        }
                        return Tuple0.INSTANCE;
                    }
                }
        ));
    }

    @Override
    public R evaluate() {
        return synchronousFunction0.get();
    }

    public <T> void chain(final AsyncFunction0<T> nextBlock) {
        addCallback(new Function1<Tuple0, Future<R>>() {
            @Override
            public Tuple0 evaluate(final Function1<Tuple0, Future<R>> self,
                                   final Future<R> i1) {
                nextBlock.invokeAsync();
                return Tuple0.INSTANCE;
            }
        });
    }
}
