package com.pon.ents.base.closeable;

import java.util.function.Supplier;

/**
 * A {@link Supplier} of resources that need {@link RuntimeCloseable#close() closing}.
 */
public interface Opener<T extends RuntimeCloseable> extends Supplier<T> {

    /**
     * Opens a new instance of a resource.
     */
    T open();

    @Override
    default T get() {
        return open();
    }
}
