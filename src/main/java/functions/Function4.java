package functions;

/**
 * User: msfroh
 * Date: 12-05-31
 * Time: 12:56 AM
 */
public abstract class Function4<R, T1, T2, T3, T4> extends Curryable<T1, Function3<R, T2, T3, T4>> {
    @Override
    public Function1<Function3<R, T2, T3, T4>, T1> curry() {
        return new Function1<Function3<R, T2, T3, T4>, T1>() {
            @Override
            public Function3<R, T2, T3, T4> evaluate(final Function1<Function3<R, T2, T3, T4>, T1> self, final T1 i1) {
                return new Function3<R, T2, T3, T4>() {
                    @Override
                    public R evaluate(final Function3<R, T2, T3, T4> self, final T2 i2, final T3 i3, final T4 i4) {
                        return Function4.this.evaluate(Function4.this, i1, i2, i3, i4);
                    }
                };
            }
        };
    }

    public abstract R evaluate(final Function4<R, T1, T2, T3, T4> self,
            final T1 i1, final T2 i2, final T3 i3, final T4 i4);

    public R evaluate(final T1 i1, final T2 i2, final T3 i3, final T4 i4) {
        return evaluate(this, i1, i2, i3, i4);
    }
}
