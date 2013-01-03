package concurrency;

import functions.Function0;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * User: msfroh
 * Date: 2012-12-24
 * Time: 5:13 PM
 */
public class ConcurrentEvaluator {
    private final ExecutorService executorService;

    public ConcurrentEvaluator(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <T> Function0<T> evaluateConcurrent(final Function0<T> f0) {
        final Future<T> futureVal = executorService.submit(f0);
        return new Function0<T>() {
            @Override
            public T evaluate() {
                try {
                    return futureVal.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
