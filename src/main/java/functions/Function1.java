package functions;

import tuples.Tuple1;

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


    public final Function1<R, Tuple1<T1>> tupled() {
        return new Function1<R, Tuple1<T1>>() {
            public R evaluate(Function1<R, Tuple1<T1>> self, Tuple1<T1> i1) {
                return Function1.this.evaluate(Function1.this, i1._1);
            }
        };
    }
}
