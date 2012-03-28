package functions;

import java.util.HashMap;
import java.util.Map;

public abstract class MemoizedFunction1<R, T> extends Function1<R, T> {
    private final Map<T, Function0<R>> memoizedValues =
            new HashMap<T, Function0<R>>();

    @Override
    public synchronized Function0<R> apply(final T i1) {
        if (!memoizedValues.containsKey(i1)) {
            memoizedValues.put(i1, super.apply(i1));
        }
        return memoizedValues.get(i1);
    }
}
