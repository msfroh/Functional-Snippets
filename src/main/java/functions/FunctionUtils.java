package functions;

import java.util.Map;
import java.util.Set;

/**
 * User: froh
 * Date: 1/6/12
 * Time: 3:15 PM
 */
public class FunctionUtils {

    public static <T> Function1<Boolean, T> fromSet(final Set<T> set) {
        return new Function1<Boolean, T>() {
            @Override
            public Boolean apply(T i1) {
                return set.contains(i1);
            }
        };
    }

    public static <R, T> Function1<R, T> fromMap(final Map<T, R> map) {
        return new Function1<R, T>() {
            @Override
            public R apply(T i1) {
                return map.get(i1);
            }
        };
    }

}
