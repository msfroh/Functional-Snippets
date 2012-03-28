package functions;

/**
 * User: froh
 * Date: 1/6/12
 * Time: 3:17 PM
 */
public abstract class Curryable<T, F> {
    public abstract Function1<F, T> curry();

    public F apply(T t) {
        return curry().apply(t).get();
    }

    public F apply(Function0<T> t) {
        return curry().apply(t.get()).get();
    }
}
