package com.pon.ents.base.repo;

import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import com.pon.ents.base.repo.impl.RepositoryEntry;

/**
 * An association of keys and immutable, non-{@literal null} values.
 */
public interface Repository<K, V> {

    /**
     * Returns a value associated with the given key, if any, or throws a {@link NoSuchElementException} otherwise.
     */
    V get(K key);

    /**
     * Associates the key with the given value (replacing the previous value, if any).
     */
    void set(K key, V value);

    /**
     * Streams all the currently maintained keys.
     */
    Stream<K> streamKeys();

    /**
     * Streams live (at least in the default implementation) {@link Entry entries} currently held by this
     * {@link Repository}.
     * <p>
     * Being "live" means that each {@link Entry#getValue() value} access effectively performs a
     * {@link Repository#get(Object)}.
     * <p>
     * The {@link Entry#setValue(Object)} method, while supported, may be unnecessarily expensive (because of the
     * "return previous value" contract, which, in this case, is not even guaranteed to be atomic).
     */
    default Stream<Entry<K, V>> streamEntries() {
        return streamKeys().map(key -> new RepositoryEntry<>(this, key));
    }
}
