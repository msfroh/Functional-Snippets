package collections;

import functions.Function1;
import functions.Function2;
import tuples.Tuple2;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static tuples.TupleUtils.tuple;

/**
 * User: msfroh
 * Date: 12-06-02
 * Time: 12:32 AM
 */
public abstract class ImmutableHashTrieMap<K, V> extends Function1<Option<V>, K>
        implements ImmutableMap<K,V>, AugmentedIterable<Tuple2<K, V>>  {

    @Override
    public ImmutableHashTrieMap<K, V> put(K key, V value) {
        return put(0, key, value);
    }

    @Override
    public ImmutableHashTrieMap<K, V> remove(K key) {
        return remove(0, key);
    }

    @Override
    public Option<V> get(K key) {
        return get(0, key);
    }

    @Override
    public Option<V> evaluate(final Function1<Option<V>, K> self, final K i1) {
        return get(i1);
    }

    /*
    * In the methods below, "shift" denotes how far (in bits) through the
    * hash code we are currently looking. At each level, we add "bits",
    * defined below, to shift.
    */

    private static final int bits = 5;
    private static final int bf = 1 << bits;
    private static final int mask = bf - 1;

    abstract ImmutableHashTrieMap<K, V> put(int shift, K key,
                                            V value);

    abstract ImmutableHashTrieMap<K, V> remove(int shift, K key);

    abstract Option<V> get(int shift, K key);

    abstract boolean isArrayNode();

    @Override
    public <R> ImmutableList<R> map(final Function1<R, Tuple2<K, V>> f) {
        // Just return a list. With a runtime check on return values, we might
        // be able to return a new map if the function returns Tuple2, but
        // that will likely lead to confusion.
        ImmutableList<R> returnValue = ImmutableList.nil();
        for (Tuple2<K, V> entry : this) {
            returnValue = returnValue.prepend(f.apply(entry).get());
        }
        return returnValue;
    }

    @Override
    public <R> R foldLeft(final Function2<R, R, Tuple2<K, V>> f,
                          final R seed) {
        R returnValue = seed;
        for (Tuple2<K, V> entry : this) {
            returnValue = f.apply(returnValue, entry).get();
        }
        return returnValue;
    }

    @Override
    public <R> R foldRight(final Function2<R, Tuple2<K, V>, R> f,
                           final R seed) {
        // Since traversal order is unspecified, treat foldRight as equivalent
        // to foldLeft.
        return foldLeft(f.flip(), seed);
    }

    @Override
    public <R> ImmutableList<R>
    flatMap(final Function1<? extends Iterable<R>, Tuple2<K, V>> f) {
        // Just return a list. With a runtime check on return values, we might
        // be able to return a new map if the function returns Iterable<Tuple2>,
        // but that will likely lead to confusion.
        ImmutableList<R> returnValue = ImmutableList.nil();
        for (Tuple2<K, V> entry : this) {
            for (R value : f.apply(entry).get()) {
                returnValue = returnValue.prepend(value);
            }
        }
        return returnValue;
    }

    @Override
    public ImmutableHashTrieMap<K, V>
    filter(final Function1<Boolean, Tuple2<K, V>> predicate) {
        ImmutableHashTrieMap<K, V> newMap = empty();
        for (Tuple2<K, V> entry : this) {
            if (predicate.apply(entry).get()) {
                newMap.put(entry._1, entry._2);
            }
        }
        return newMap;
    }

    private static class EmptyHashNode<K, V>
            extends ImmutableHashTrieMap<K, V> {
        @Override
        ImmutableHashTrieMap<K, V> put(final int shift, final K key,
                                       final V value) {
            return new EntryHashNode<K, V>(key, value);
        }

        @Override
        ImmutableHashTrieMap<K, V> remove(final int shift,
                                          final K key) {
            return this;
        }

        @Override
        boolean isArrayNode() {
            return false;
        }

        @Override
        Option<V> get(final int shift, final K key) {
            return Option.none();
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return Collections.<Tuple2<K, V>>emptySet().iterator();
        }
    }

    private static final ImmutableHashTrieMap EMPTY_NODE = new EmptyHashNode();

    public static <K, V> ImmutableHashTrieMap<K, V> empty() {
        return EMPTY_NODE;
    }

    private static class EntryHashNode<K, V>
            extends ImmutableHashTrieMap<K, V> {
        private final K key;
        private final V value;

        private EntryHashNode(final K key,
                              final V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        ImmutableHashTrieMap<K, V> put(final int shift, final K key,
                                       final V value) {
            if (this.key.equals(key)) {
                // Overwriting this entry
                return new EntryHashNode<K, V>(key, value);
            } else if (this.key.hashCode() == key.hashCode()) {
                // This is a collision. Return a new ListHashNode.
                return new ListHashNode<K, V>(tuple(this.key, this.value),
                        tuple(key, value));
            }
            // Split this node into an ArrayHashNode with this and the new value
            // as entries.
            return newArrayHashNode(shift, this.key.hashCode(), this,
                    key.hashCode(), new EntryHashNode<K, V>(key, value));
        }

        @Override
        ImmutableHashTrieMap<K, V> remove(final int shift,
                                          final K key) {
            if (this.key.equals(key)) {
                return empty();
            }
            return this;
        }

        @Override
        boolean isArrayNode() {
            return false;
        }

        @Override
        Option<V> get(final int shift, final K key) {
            if (this.key.equals(key)) {
                return Option.option(value);
            }
            return Option.none();
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return Collections.singleton(tuple(key, value)).iterator();
        }
    }

    private static class ListHashNode<K, V> extends ImmutableHashTrieMap<K, V> {
        private final ImmutableList<Tuple2<K, V>> entries;

        public ListHashNode(Tuple2<K, V> entry1,
                            Tuple2<K, V> entry2) {
            // These entries must collide
            assert entry1._1.hashCode() == entry2._1.hashCode();
            entries = ImmutableList.<Tuple2<K, V>>nil().prepend(entry1)
                    .prepend(entry2);
        }

        private ListHashNode(final ImmutableList<Tuple2<K, V>> entries) {
            // Size should be at least 2
            assert !entries.isEmpty();
            assert !entries.tail().isEmpty();
            this.entries = entries;
        }

        @Override
        ImmutableHashTrieMap<K, V> put(final int shift, final K key,
                                       final V value) {
            if (entries.head()._1.hashCode() != key.hashCode()) {
                return newArrayHashNode(shift,
                        entries.head()._1.hashCode(),
                        this,
                        key.hashCode(),
                        new EntryHashNode<K, V>(
                                key, value));
            }
            ImmutableList<Tuple2<K, V>> newList = ImmutableList.nil();
            boolean found = false;
            for (Tuple2<K, V> entry : entries) {
                if (entry._1.equals(key)) {
                    // Node replacement
                    newList =
                            newList.prepend(tuple(key, value));
                    found = true;
                } else {
                    newList = newList.prepend(entry);
                }
            }
            if (!found) {
                // Adding a new entry
                newList = newList.prepend(tuple(key, value));
            }
            return new ListHashNode<K, V>(newList);
        }

        @Override
        ImmutableHashTrieMap<K, V> remove(final int shift,
                                          final K key) {
            ImmutableList<Tuple2<K, V>> newList = ImmutableList.nil();
            int size = 0;
            for (Tuple2<K, V> entry : entries) {
                if (!entry._1.equals(key)) {
                    newList = newList.prepend(entry);
                    size++;
                }
            }
            if (size == 1) {
                Tuple2<K, V> entry = newList.head();
                return new EntryHashNode<K, V>(entry._1, entry._2);
            }
            return new ListHashNode<K, V>(newList);
        }

        @Override
        boolean isArrayNode() {
            return false;
        }

        @Override
        Option<V> get(final int shift, final K key) {
            for (Tuple2<K, V> entry : entries) {
                if (entry._1.equals(key)) {
                    return Option.option(entry._2);
                }
            }
            return Option.none();
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return new Iterator<Tuple2<K, V>>() {
                private ImmutableList<Tuple2<K, V>> curList =
                        ListHashNode.this.entries;

                @Override
                public boolean hasNext() {
                    return !curList.isEmpty();
                }

                @Override
                public Tuple2<K, V> next() {
                    Tuple2<K, V> retVal = curList.head();
                    curList = curList.tail();
                    return retVal;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private static <K, V> ImmutableHashTrieMap<K, V>
        newArrayHashNode(int shift,
                         int hash1,
                         ImmutableHashTrieMap<K, V> subNode1,
                         int hash2,
                         ImmutableHashTrieMap<K, V> subNode2) {
        int curShift = shift;
        int h1 = hash1 >> shift & mask;
        int h2 = hash2 >> shift & mask;
        List<Integer> buckets = new LinkedList<Integer>();
        while (h1 == h2) {
            buckets.add(0, h1);
            curShift += bits;
            h1 = hash1 >> curShift & mask;
            h2 = hash2 >> curShift & mask;
        }
        ImmutableHashTrieMap<K, V> newNode = new BranchedArrayHashNode<K, V>(h1,
                subNode1,
                h2,
                subNode2);
        for (Integer bucket : buckets) {
            newNode = new SingletonArrayHashNode<K, V>(bucket, newNode);
        }
        return newNode;

    }

    private static abstract class ArrayHashNode<K,V>
            extends ImmutableHashTrieMap<K,V> {
        @Override
        boolean isArrayNode() {
            return true;
        }
    }

    private static <K> int getBucket(int shift, K key) {
        return key.hashCode() >> shift & mask;
    }

    private static class BranchedArrayHashNode<K, V>
            extends ArrayHashNode<K, V> {
        private final ImmutableHashTrieMap<K, V>[] subnodes;
        private final int size;

        public BranchedArrayHashNode(int h1,
                             ImmutableHashTrieMap<K, V> subNode1,
                             int h2,
                             ImmutableHashTrieMap<K, V> subNode2) {
            assert h1 != h2;
            size = 2;
            subnodes = new ImmutableHashTrieMap[bf];
            for (int i = 0; i < bf; i++) {
                if (i == h1) {
                    subnodes[i] = subNode1;
                } else if (i == h2) {
                    subnodes[i] = subNode2;
                } else {
                    subnodes[i] = EMPTY_NODE;
                }
            }
        }

        public BranchedArrayHashNode(int size,
                             final ImmutableHashTrieMap<K, V>[] subnodes) {
            assert subnodes.length == bf;
            this.size = size;
            this.subnodes = subnodes;
        }

        @Override
        ImmutableHashTrieMap<K, V> put(final int shift, final K key,
                                       final V value) {
            final int bucket = getBucket(shift, key);
            ImmutableHashTrieMap<K, V>[] newNodes = new ImmutableHashTrieMap[bf];
            System.arraycopy(subnodes, 0, newNodes, 0, bf);

            final int newSize =
                    newNodes[bucket] == EMPTY_NODE ? size + 1 : size;
            newNodes[bucket] = newNodes[bucket].put(shift + bits,
                    key, value);
            return new BranchedArrayHashNode<K, V>(newSize, newNodes);
        }

        @Override
        ImmutableHashTrieMap<K, V> remove(final int shift,
                                          final K key) {
            final int bucket = getBucket(shift, key);
            if (subnodes[bucket] == EMPTY_NODE) {
                return this;
            }
            ImmutableHashTrieMap<K, V>[] newNodes = new ImmutableHashTrieMap[bf];
            System.arraycopy(subnodes, 0, newNodes, 0, bf);
            newNodes[bucket] = newNodes[bucket].remove(shift + bits,
                    key);
            final int newSize =
                    newNodes[bucket] == EMPTY_NODE ? size - 1 : size;
            if (newSize == 1) {
                int orphanedBucket = -1;
                for (int i = 0; i < bf; i++) {
                    if (newNodes[i] != EMPTY_NODE) {
                        orphanedBucket = i;
                        break;
                    }
                }
                ImmutableHashTrieMap<K, V> orphanedEntry =
                        subnodes[orphanedBucket];
                if (orphanedEntry.isArrayNode()) {
                    return new SingletonArrayHashNode<K, V>(orphanedBucket,
                            orphanedEntry);
                }
                return orphanedEntry;
            }
            return new BranchedArrayHashNode<K, V>(newSize, newNodes);
        }

        @Override
        Option<V> get(final int shift, final K key) {
            final int bucket = getBucket(shift, key);
            return subnodes[bucket].get(shift + bits, key);
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return new Iterator<Tuple2<K, V>>() {
                private int bucket = 0;
                private Iterator<Tuple2<K, V>> childIterator =
                        subnodes[0].iterator();

                @Override
                public boolean hasNext() {
                    if (childIterator.hasNext()) {
                        return true;
                    }
                    bucket++;
                    while (bucket < bf) {
                        childIterator = subnodes[bucket].iterator();
                        if (childIterator.hasNext()) {
                            return true;
                        }
                        bucket++;
                    }
                    return false;
                }

                @Override
                public Tuple2<K, V> next() {
                    return childIterator.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private static class SingletonArrayHashNode<K, V> extends
            ArrayHashNode<K, V> {
        private final int bucket;
        private final ImmutableHashTrieMap<K, V> subnode;

        private SingletonArrayHashNode(final int bucket,
                                       final ImmutableHashTrieMap<K, V> subnode) {
            assert subnode instanceof ArrayHashNode;
            this.bucket = bucket;
            this.subnode = subnode;
        }

        @Override
        ImmutableHashTrieMap<K, V> put(final int shift, final K key,
                                       final V value) {
            final int bucket = getBucket(shift, key);
            if (bucket == this.bucket) {
                return new SingletonArrayHashNode<K, V>(bucket,
                        subnode.put(shift + bits, key, value));
            }
            return new BranchedArrayHashNode<K, V>(this.bucket, subnode,
                    bucket, new EntryHashNode<K, V>(key, value));
        }

        @Override
        ImmutableHashTrieMap<K, V> remove(final int shift, final K key) {
            final int bucket = getBucket(shift, key);
            if (bucket == this.bucket) {
                ImmutableHashTrieMap<K, V> newNode =
                        subnode.remove(shift + bits, key);
                if (!(newNode.isArrayNode())) {
                    return newNode;
                }
                return new SingletonArrayHashNode<K, V>(bucket, newNode);
            }
            return this;
        }

        @Override
        Option<V> get(final int shift, final K key) {
            final int bucket = getBucket(shift, key);
            if (bucket == this.bucket) {
                return subnode.get(shift + bits, key);
            }
            return Option.none();
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return subnode.iterator();
        }
    }

}
