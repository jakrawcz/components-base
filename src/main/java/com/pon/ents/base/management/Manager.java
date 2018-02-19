package com.pon.ents.base.management;

import java.util.Map.Entry;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

/**
 * An association of immutable keys and mutable, non-{@literal null} objects of a specific type.
 * <p>
 * An object for every key always exists - but only some of them are not <b>empty</b>. A notion of <b>emptiness</b> is
 * implementation-specific (ideally it should be only {@code T}-specific) and a {@link Manager} is aware of which
 * objects are not <b>empty</b>.
 */
public interface Manager<K, T> {

    /**
     * Returns an object associated with the given key.
     */
    T access(K key);

    /**
     * Streams the keys for those objects that are currently not <b>empty</b> (as defined by the {@link Manager}'s
     * contract).
     */
    Stream<K> streamNonEmptyObjectKeys();

    /**
     * Streams the {@link Entry entries} (keys and objects) for all not <b>empty</b> objects.
     */
    default Stream<Entry<K, T>> streamNonEmptyBlobEntries() {
        return streamNonEmptyObjectKeys().map(key -> Maps.immutableEntry(key, access(key)));
    }
}
