package functions;

public abstract class MemoizedFunction3<R, T1, T2, T3>
        extends Function3<R, T1, T2, T3> {
    private final Function1<Function2<R, T2, T3>, T1> memoizedCurry =
            new MemoizedFunction1<Function2<R, T2, T3>, T1>() {
                @Override
                public Function2<R, T2, T3>
                evaluate(final Function1<Function2<R, T2, T3>, T1> self,
                                                     final T1 i1) {
                    return new MemoizedFunction2<R, T2, T3>() {
                        @Override
                        public R evaluate(final Function2<R, T2, T3> self,
                                          final T2 i2, final T3 i3) {
                            return MemoizedFunction3.this.evaluate(i1, i2, i3);
                        }
                    };
                }
            };

    @Override
    public Function1<Function2<R, T2, T3>, T1> curry() {
        return memoizedCurry;
    }
}
