package functions;

import tuples.Tuple2;

public abstract class Function2<R, T1, T2> {
    public abstract R apply(final T1 i1, final T2 i2);

    /**
     * @param i1 first parameter to be bound
     * @return a function of one parameter (a version of this function where the first parameter has
     *  been bound to i1).
     */
    public Function1<R, T2> apply(final T1 i1) {
        return curry().apply(i1);
    }

    public Function1<R, T2> apply(final Function0<T1> i1) {
        return curry().apply(i1);
    }

    public final Function1<Function1<R, T2>, T1> curry() {
        return new Function1<Function1<R, T2>, T1>() {
            public Function1<R, T2> apply(final T1 i1) {
                return new Function1<R, T2>() {
                    @Override
                    public R apply(final T2 i2) {
                        return Function2.this.apply(i1, i2);
                    }
                };
            }
        };
    }

    public Function2<R, T2, T1> flip() {
        return new Function2<R, T2, T1>() {
            public R apply(final T2 i1, final T1 i2) {
                return Function2.this.apply(i2, i1);
            }
        };
    }

    public final Function1<R, Tuple2<T1, T2>> tupled() {
        return new Function1<R, Tuple2<T1, T2>>() {
            @Override
            public R apply(Tuple2<T1, T2> i1) {
                return Function2.this.apply(i1._1(), i1._2());
            }
        };
    }
}
