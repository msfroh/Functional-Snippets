package functions;

//import tuples.Tuple3;

public abstract class Function3<R, T1, T2, T3>
        extends Curryable<T1, Function2<R, T2, T3>>{
    public abstract R evaluate(final Function3<R, T1, T2, T3> self,
            final T1 i1, final T2 i2, T3 i3);

    public R evaluate(final T1 i1, final T2 i2, T3 i3) {
        return evaluate(this, i1, i2, i3);
    }

    @Override
    public Function1<Function2<R, T2, T3>, T1> curry() {
        return new Function1<Function2<R, T2, T3>, T1>() {
            public Function2<R, T2, T3> evaluate(final Function1<Function2<R, T2, T3>, T1> self,
                                                 final T1 i1) {
                return new Function2<R, T2, T3>() {
                    @Override
                    public R evaluate(Function2<R, T2, T3> self, final T2 i2, final T3 i3) {
                        return Function3.this.evaluate(Function3.this, i1, i2, i3);
                    }
                };
            }
        };
    }

    public final Function0<R> apply(final T1 i1, final T2 i2, final T3 i3) {
        return apply(i1).apply(i2).apply(i3);
    }


//    public final Function1<R, Tuple3<T1, T2, T3>> tupled() {
//        return new Function1<R, Tuple3<T1, T2, T3>>() {
//            @Override
//            public R apply(Tuple3<T1, T2, T3> i1) {
//                return Function3.this.apply(i1._1, i1._2, i1._3);
//            }
//        };
//    }
}
