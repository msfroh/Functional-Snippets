package collections;

import functions.Function1;
import functions.Function2;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * User: froh
 * Date: 4/5/12
 * Time: 12:29 PM
 */
public abstract class Option<T> implements AugmentedIterable<T> {
    public abstract T get();

    public abstract boolean isDefined();

    public final T getOrElse(T defaultVal) {
        return isDefined() ? get() : defaultVal;
    }

    // Specialize return types for these AugmentedIterable methods
    public final <R> Option<R> map(Function1<R, T> f) {
        return isDefined() ? option(f.evaluate(get())) : Option.<R>none();
    }

    public final <R> Option<R> flatMap(Function1<? extends Iterable<R>, T> f) {
        if (!isDefined()) {
            return none();
        }

        Iterable<? extends R> val = f.evaluate(get());
        Iterator<? extends R> iter = val.iterator();
        if (iter.hasNext()) {
            Option<R> result = option(iter.next());
            if (iter.hasNext()) {
                // Option.flatMap only works with functions that return at most
                // one element.
                throw new RuntimeException("Function passed to Option flatMap " +
                        "returns more than one element");
            }
            return result;
        }
        return none();
    }

    public final Option<T> filter(Function1<Boolean, T> predicate) {
        if (isDefined() && predicate.evaluate(get())) {
            return this;
        }
        return none();
    }

    @Override
    public final <R> R foldLeft(final Function2<R, R, T> f, final R seed) {
        return isDefined() ? f.evaluate(seed, get()) : seed;
    }

    @Override
    public final <R> R foldRight(final Function2<R, T, R> f, final R seed) {
        return isDefined() ? f.evaluate(get(), seed) : seed;
    }

    @Override
    public Iterator<T> iterator() {
        return isDefined() ? Collections.singleton(get()).iterator() :
                Collections.<T>emptySet().iterator();
    }

    public static <T> Option<T> option(T value) {
        if (value == null) {
            return none();
        }
        return some(value);
    }

    // Factory method to return the singleton None instance
    @SuppressWarnings({"unchecked"})
    public static <T> Option<T> none() {
        return NONE;
    }

    // Factory method to return a non-empty Some instance
    public static <T> Option<T> some(final T value) {
        return new Some<T>(value);
    }

    private static None NONE = new None();

    private static class None extends Option {

        @Override
        public Object get() {
            throw new NoSuchElementException("get() called on None");
        }

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public String toString() {
            return "None";
        }
    }

    private static class Some<T> extends Option<T> {
        private final T value;

        public Some(final T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }

        public boolean equals(Object other) {
            return other instanceof Some && ((Some) other).value.equals(value);
        }
    }
}
