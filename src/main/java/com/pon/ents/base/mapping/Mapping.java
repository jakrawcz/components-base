package com.pon.ents.base.mapping;

import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;

/**
 * A set of unique-keyed {@link Entry entries} with a (presumably) optimized {@link #find} operation.
 * <p>
 * No {@literal null}s are allowed (as keys or values).
 */
public interface Mapping<K, V> {

    /**
     * Streams all the contained {@link Entry entries}.
     */
    Stream<Entry<K, V>> streamEntries();

    /**
     * Returns the {@link Entry entries} as {@link Iterable}.
     */
    default Iterable<Entry<K, V>> entries() {
        return () -> streamEntries().iterator();
    }

    /**
     * Returns the {@link Entry entries} as {@link Iterable}.
     */
    default Iterable<K> keys() {
        return Iterables.transform(entries(), Entry::getKey);
    }

    /**
     * Returns the {@link Entry entries} as {@link Iterable}.
     */
    default Iterable<V> values() {
        return Iterables.transform(entries(), Entry::getValue);
    }

    /**
     * Returns a value for the given key, if present, or {@literal null} otherwise.
     */
    @Nullable
    default V find(K key) {
        for (Entry<K, V> e : entries()) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * Returns a value for the given key, if present, or throws a {@link NoSuchElementException} otherwise.
     */
    default V get(K key) {
        @Nullable V value = find(key);
        if (value == null) {
            throw new NoSuchElementException("for " + key);
        }
        return value;
    }

    /**
     * Returns an {@link Optional} value for the given key.
     */
    default Optional<V> getOptional(K key) {
        return Optional.ofNullable(find(key));
    }
}
