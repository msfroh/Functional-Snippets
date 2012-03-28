package functions;

public class MemoizationUtils {

    public static <R, T1> MemoizedFunction1<R, T1>
      memoize(final Function1<R, T1> f) {
        return new MemoizedFunction1<R, T1> (){
            @Override
            public R evaluate(final Function1<R, T1> self, final T1 i1) {
                return f.evaluate(this, i1);
            }
        };
    }

    public static <R, T1, T2> MemoizedFunction2<R, T1, T2>
      memoize(final Function2<R, T1, T2> f) {
        return new MemoizedFunction2<R, T1, T2>() {
            @Override
            public R evaluate(Function2<R, T1, T2> self,
                              final T1 i1, final T2 i2) {
                return f.evaluate(this, i1, i2);
            }
        };
    }

    public static <R,T1,T2,T3> MemoizedFunction3<R, T1, T2, T3>
      memoize(final Function3<R, T1, T2, T3> f) {
        return new MemoizedFunction3<R, T1, T2, T3>() {
            @Override
            public R evaluate(Function3<R, T1, T2, T3> self,
                              final T1 i1, final T2 i2, final T3 i3) {
                return f.evaluate(this, i1, i2, i3);
            }
        };
    }
}
