package functions;

import tuples.Tuple1;

public abstract class Function1<R, T1> {
    public abstract R apply(final T1 i1);

    public final R apply(final Function0<T1> fi1) {
        return apply(fi1.apply());
    }

    public final Function0<R> lazyApply(final T1 i1) {
        return Function0.lazy(new Function0<R>() {
            @Override
            public R apply() {
                return Function1.this.apply(i1);
            }
        });
    }

    public final Function0<R> lazyApply(final Function0<T1> i1) {
        return Function0.lazy(new Function0<R>() {
            @Override
            public R apply() {
                return Function1.this.apply(i1);
            }
        });
    }

    public final Function1<R, Tuple1<T1>> tupled() {
        return new Function1<R, Tuple1<T1>>() {
            public R apply(Tuple1<T1> i1) {
                return Function1.this.apply(i1._1);
            }
        };
    }
}
