package functions;

import concurrency.AsyncFunction0;
import tuples.Tuple0;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Function1<R, T1> {
    public R evaluate(final T1 i1) {
        return evaluate(this, i1);
    }

    public abstract R evaluate(Function1<R, T1> self, final T1 i1);

    public Function0<R> apply(final T1 i1) {
        return new Function0<R>() {
            @Override
            public R evaluate() {
                return Function1.this.evaluate(i1);
            }
        };
    }

    /*
     * Lazier-than-lazy version of apply.
     *
     * Doesn't call apply() on its parameter until apply() is called on
     * the returned Function0.
     */
    public Function0<R> apply(final Function0<T1> fi1) {
        return new Function0<R>() {
            @Override
            public R evaluate() {
                return Function1.this.evaluate(Function1.this, fi1.get());
            }
        };
    }

    public Function0<R> apply(final AsyncFunction0<T1> fi1) {
        final BlockingQueue<Future<T1>> answerQueue =
                new LinkedBlockingQueue<>();
        fi1.addCallback(new Function1<Tuple0, Future<T1>>(){
            public Tuple0 evaluate(final Function1<Tuple0, Future<T1>> self,
                                   final Future<T1> i1) {
                answerQueue.add(i1);
                return Tuple0.INSTANCE;
            }
        });
        return new Function0<R>() {
            @Override
            public R evaluate() {
                try {
                    return Function1.this.apply(answerQueue.take().get()).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public <R2> Function1<R2, T1> compose(final Function1<R2, R> f) {
        return new Function1<R2, T1>() {
            @Override
            public R2 evaluate(final Function1<R2, T1> self, final T1 i1) {
                return f.evaluate(f, Function1.this.evaluate(Function1.this, i1));
            }
        };
    }
}
