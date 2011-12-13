package functions;

public abstract class Function3<R, T1, T2, T3> {
    public abstract R apply(final T1 i1, final T2 i2, T3 i3);

    /**
     * @param i1 first parameter to be bound
     * @return a function of one parameter (a version of this function where the first parameter has
     *         been bound to i1).
     */
    public Function2<R, T2, T3> apply(final T1 i1) {
        return curry().apply(i1);
    }

    public Function2<R, T2, T3> apply(final Function0<T1> i1) {
        return curry().apply(i1);
    }

    public final Function1<Function2<R, T2, T3>, T1> curry() {
        return new Function1<Function2<R, T2, T3>, T1>() {
            public Function2<R, T2, T3> apply(final T1 i1) {
                return new Function2<R, T2, T3>() {
                    @Override
                    public R apply(final T2 i2, final T3 i3) {
                        return Function3.this.apply(i1, i2, i3);
                    }
                };
            }
        };
    }
}
