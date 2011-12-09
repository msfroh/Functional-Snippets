package tuples;

public final class Tuple2<T1,T2> {
    private final T1 val1;
    private final T2 val2;

    public Tuple2(final T1 value1, final T2 value2) {
        val1 = value1;
        val2 = value2;
    }

    public T1 _1() {
        return val1;
    }

    public T2 _2() {
        return val2;
    }
}
