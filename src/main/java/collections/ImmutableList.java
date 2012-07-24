package collections;

import functions.Function1;
import functions.Function2;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class ImmutableList<T> implements AugmentedIterable<T> {
    public abstract T head();

    public abstract ImmutableList<T> tail();

    public abstract boolean isEmpty();

    public final int size() {
        ImmutableList<T> input = this;
        int size = 0;
        while (!input.isEmpty()) {
            size++;
            input = input.tail();
        }
        return size;
    }


    public ImmutableList<T> prepend(T element) {
        return new NonEmptyList<T>(element, this);
    }

    public static <T> ImmutableList<T> nil() {
        return EMPTY_LIST;
    }

    public static <T> ImmutableList<T> list(T... elements) {
        ImmutableList<T> output = nil();
        for (int i = elements.length - 1; i >= 0; i--) {
            output = output.prepend(elements[i]);
        }
        return output;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ImmutableList)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Iterator<T> thisIter = this.iterator();
        Iterator<T> otherIter = ((ImmutableList) o).iterator();
        while (thisIter.hasNext() && otherIter.hasNext()) {
            if (!thisIter.next().equals(otherIter.next())) {
                return false;
            }
        }
        return thisIter.hasNext() == otherIter.hasNext();
    }

    public ImmutableList<T> reverse() {
        ImmutableList<T> input = this;
        ImmutableList<T> output = nil();
        while (input != nil()) {
            output = output.prepend(input.head());
            input = input.tail();
        }
        return output;
    }

    @Override
    public final <R> ImmutableList<R> map(Function1<R, T> f) {
        ImmutableList<T> input = this;
        ImmutableList<R> output = nil();
        // This traversal constructs the output list by
        // prepending, yielding a list in reverse order.
        while (!input.isEmpty()) {
            output = output.prepend(f.evaluate(input.head()));
            input = input.tail();
        }
        // Use reverse to get back the original order.
        return output.reverse();
    }

    @Override
    public final <R> R foldLeft(Function2<R, R, T> f, R seed) {
        ImmutableList<T> input = this;
        R output = seed;
        while (!input.isEmpty()) {
            output = f.evaluate(output, input.head());
            input = input.tail();
        }
        return output;
    }

    @Override
    public final <R> R foldRight(Function2<R, T, R> f, R seed) {
        // Reverse the list so that we can traverse from right to left
        // by traversing from left to right, effectively reducing the
        // problem to foldLeft.
        ImmutableList<T> input = this.reverse();
        R output = seed;
        while (!input.isEmpty()) {
            output = f.evaluate(input.head(), output);
            input = input.tail();
        }
        return output;
    }

    @Override
    public final ImmutableList<T> filter(Function1<Boolean, T> predicate) {
        ImmutableList<T> input = this;
        ImmutableList<T> output = nil();
        // This traversal constructs the output list by
        // prepending, yielding a list in reverse order.
        while (!input.isEmpty()) {
            if (predicate.evaluate(input.head())) {
                output = output.prepend(input.head());
            }
            input = input.tail();
        }
        // Use reverse to get back the original order.
        return output.reverse();
    }

    public final <R> ImmutableList<R>
    flatMap(Function1<? extends Iterable<R>, T> f) {
        ImmutableList<T> input = this;
        ImmutableList<R> output = nil();
        while (!input.isEmpty()) {
            // Unlike with map(), we know that the return value
            // from f is itself iterable. So, we iterate and
            // accumulate those values.
            Iterable<R> rs = f.evaluate(input.head());
            for (R r : rs) {
                output = output.prepend(r);
            }
            input = input.tail();
        }
        return output.reverse();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private ImmutableList<T> list = ImmutableList.this;

            @Override
            public boolean hasNext() {
                return !list.isEmpty();
            }

            @Override
            public T next() {
                T element = list.head();
                list = list.tail();
                return element;
            }

            @Override
            public void remove() {
                throw new RuntimeException("Cannot remove from immutable list");
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (T elem : this) {
            if (!first) builder.append(", ");
            builder.append(elem);
            first = false;
        }
        builder.append(']');
        return builder.toString();
    }

    private static final EmptyList EMPTY_LIST = new EmptyList();

    private static class EmptyList extends ImmutableList {
        @Override
        public Object head() {
            throw new NoSuchElementException("head() called on empty list");
        }

        @Override
        public ImmutableList tail() {
            throw new NoSuchElementException("head() called on empty list");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    private static class NonEmptyList<T> extends ImmutableList<T> {
        private final T element;
        private final ImmutableList<T> tail;

        private NonEmptyList(final T element, final ImmutableList<T> tail) {
            this.element = element;
            this.tail = tail;
        }

        @Override
        public T head() {
            return element;
        }

        @Override
        public ImmutableList<T> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
