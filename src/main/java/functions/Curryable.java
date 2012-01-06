package functions;

/**
 * User: froh
 * Date: 1/6/12
 * Time: 3:17 PM
 */
public abstract class Curryable<T1, F> {
    public abstract Function1<F, T1> curry();

    public F apply(T1 t1) {
        return curry().apply(t1);
    }

    public F apply(Function0<T1> t1) {
        return curry().apply(t1);
    }
}
