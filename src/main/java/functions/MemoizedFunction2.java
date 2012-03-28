package functions;

public abstract class MemoizedFunction2<R, T1, T2>
        extends Function2<R, T1, T2> {
    private final Function1<Function1<R, T2>, T1> memoizedCurry =
            new MemoizedFunction1<Function1<R, T2>, T1>() {
        @Override
        public Function1<R, T2> evaluate(final Function1<Function1<R, T2>, T1> self,
                                         final T1 i1) {
            return new MemoizedFunction1<R, T2>() {
                @Override
                public R evaluate(final Function1<R, T2> self, final T2 i2) {
                    return MemoizedFunction2.this.evaluate(MemoizedFunction2.this,
                            i1, i2);
                }
            };
        }
    };
    @Override
    public Function1<Function1<R, T2>, T1> curry() {
        return memoizedCurry;
    }

}
