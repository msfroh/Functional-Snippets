package functions;

import collections.Option;

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
            public Boolean evaluate(Function1<Boolean, T> self, T i1) {
                return set.contains(i1);
            }
        };
    }

    public static <R, T> Function1<R, T> fromMap(final Map<T, R> map) {
        return new Function1<R, T>() {
            @Override
            public R evaluate(Function1<R, T> self, T i1) {
                return map.get(i1);
            }
        };
    }

    public static <R, T> Function1<Option<R>, Option<T>>
        optional(final Function1<R, T> f) {
        return new Function1<Option<R>, Option<T>>() {
            @Override
            public Option<R> evaluate(final Function1<Option<R>,
                    Option<T>> self, final Option<T> i1) {
                //noinspection LoopStatementThatDoesntLoop
                for (T t : i1) {
                    return Option.option(f.evaluate(f, t));
                }
                return Option.none();
            }
        };
    }

}
