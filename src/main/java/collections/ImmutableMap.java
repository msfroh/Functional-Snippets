package collections;

/**
 * User: msfroh
 * Date: 12-06-29
 * Time: 11:39 PM
 */
public interface ImmutableMap<K, V> {
    /**
     * Add a new entry to the map. If an entry already exists with the
     * given key, the returned map will contain this entry, but not the
     * existing entry.
     *
     * @param key   the key to use to retrieve this item
     * @param value the value stored for this item
     * @return a new map with this item added
     */
    ImmutableMap<K, V> put(K key, V value);

    /**
     * Remove an entry from the map. If no entry exists with the given
     * key, the returned map will have identical contents to the original
     * map (and may, in fact, be the original map itself).
     *
     * @param key the key for the entry to remove
     * @return a new map with the entry with the given key removed (or
     *         a map with the original contents if no entry was found
     *         for the given key).
     */
    ImmutableMap<K, V> remove(K key);

    /**
     * Retrieve a stored value from the map based on the key for the
     * associated entry. If no entry exists with the given key, we
     * return None.
     *
     * @param key the key for the entry to retrieve
     * @return Some(value) if an entry exists with the given key, or
     *         None if no entry with the given key was found.
     */
    Option<V> get(K key);
}
