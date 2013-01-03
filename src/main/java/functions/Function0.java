package functions;

import java.util.concurrent.Callable;

public abstract class Function0<R> implements Callable<R> {
    private R value = null;

    /**
     * The get() method should be synchronized, to ensure that we don't call
     * evaluate unnecessarily in a multithreaded setting.
     *
     * @return the result of evaluating -- results will be cached
     */
    public final synchronized R get() {
        if (value == null) {
            value = evaluate();
        }
        return value;
    }

    public abstract R evaluate();

    @Override
    public R call() throws Exception {
        return get();
    }
}
