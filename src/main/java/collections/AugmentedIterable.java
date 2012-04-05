package collections;

import functions.Function1;
import functions.Function2;

/**
 * User: froh
 * Date: 4/5/12
 * Time: 1:01 PM
 */
public interface AugmentedIterable<T> extends Iterable<T> {
    <R> AugmentedIterable<R> map(Function1<R, T> f);

    <R> R foldLeft(Function2<R, R, T> f, R seed);

    <R> R foldRight(Function2<R, T, R> f, R seed);

    <R> AugmentedIterable<R> flatMap(Function1<? extends AugmentedIterable<R>, T> f);

    AugmentedIterable<T> filter(Function1<Boolean, T> predicate);
}
