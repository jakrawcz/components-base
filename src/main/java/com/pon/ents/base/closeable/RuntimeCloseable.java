package com.pon.ents.base.closeable;

/**
 * An {@link AutoCloseable} that allows only {@link RuntimeException}s.
 */
public interface RuntimeCloseable extends AutoCloseable, Runnable {

    @Override
    void close();

    @Override
    default void run() {
        close();
    }
}
