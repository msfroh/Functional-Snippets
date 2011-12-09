package tuples;

public final class Tuple1<T1> {
    private final T1 val;

    public Tuple1(final T1 value) {
        val = value;
    }

    public T1 _1() {
        return val;
    }
}
