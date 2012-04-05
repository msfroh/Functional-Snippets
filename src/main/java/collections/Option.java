package collections;

import functions.Function1;
import functions.Function2;

import java.util.Collections;
import java.util.Iterator;

/**
 * User: froh
 * Date: 4/5/12
 * Time: 12:29 PM
 */
public abstract class Option<T> implements AugmentedIterable<T> {
    public abstract T get();

    public abstract boolean isDefined();

    // Specialize return types for these AugmentedIterable methods
    public abstract <R> Option<R> map(Function1<R, T> f);
    public abstract <R> Option<R> flatMap(Function1<? extends AugmentedIterable<R>, T> f);
    public abstract Option<T> filter(Function1<Boolean, T> predicate);

    public static <T> Option<T> option(T value) {
        if (value == null) {
            return none();
        }
        return some(value);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Option<T> none() {
        return NONE;
    }

    public static <T> Option<T> some(final T value) {
        return new Some<T>(value);
    }

    private static None NONE = new None();

    private static class None extends Option {

        @Override
        public Object get() {
            throw new RuntimeException("get() called on None");
        }

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public Option map(final Function1 f) {
            return this;
        }

        @Override
        public Object foldRight(final Function2 f, final Object seed) {
            return seed;
        }

        @Override
        public Object foldLeft(final Function2 f, final Object seed) {
            return seed;
        }

        @Override
        public Iterator iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public Option flatMap(final Function1 f) {
            return this;
        }

        @Override
        public Option filter(final Function1 predicate) {
            return this;
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
        public <R> Option<R> map(final Function1<R, T> f) {
            return option(f.evaluate(value));
        }

        @Override
        public <R> R foldLeft(final Function2<R, R, T> f, final R seed) {
            return f.evaluate(seed, value);
        }

        @Override
        public <R> R foldRight(final Function2<R, T, R> f, final R seed) {
            return f.evaluate(value, seed);
        }

        @Override
        public <R> Option<R> flatMap(final Function1<? extends AugmentedIterable<R>, T> f) {
            // Option can only flatMap with functions that return other options
            AugmentedIterable<? extends R> val = f.evaluate(value);
            if (!(val instanceof Option)) {
                throw new RuntimeException("Function passed to Option flatMap returns " + val.getClass() + " not Option");
            }
            return (Option<R>) val;
        }

        @Override
        public Option<T> filter(final Function1<Boolean, T> predicate) {
            return predicate.evaluate(value) ? this : Option.<T>none();
        }

        @Override
        public Iterator<T> iterator() {
            return Collections.singleton(value).iterator();
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
