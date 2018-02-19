package com.pon.ents.base.property;

import java.util.stream.Stream;

import com.pon.ents.base.listenable.Listenable;

/**
 * A provider of named, dynamic properties.
 * <p>
 * A property of any legal name always exists - at most, it may sometimes be undefined.
 */
public interface PropertyProvider {

    /**
     * Returns a {@link Listenable} property of the given name.
     */
    Property get(String name);

    /**
     * Streams names of all the currently defined properties.
     */
    Stream<String> streamDefinedPropertyNames();
}
