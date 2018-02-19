package com.pon.ents.base.io;

import java.util.Arrays;

/**
 * A wrapper for a classic "buffer, offset and limit" that is used in {@link Input}s / {@link Output}s.
 */
public interface IoBuffer {

    /**
     * Gives a direct access to the underlying byte array.
     */
    byte[] buffer();

    /**
     * Returns an offset from which the {@link #buffer()} is allowed to be read or written.
     */
    int offset();

    /**
     * Returns a limit which the {@link #buffer()} is allowed to be read or written.
     */
    int limit();

    /**
     * Returns an instance with offset and limit changed to the given ones.
     */
    default IoBuffer rePositioned(int offset, int limit) {
        return IoBuffers.wrap(buffer(), offset, limit);
    }

    /**
     * Returns a number of readable/writable bytes.
     */
    default int length() {
        return limit() - offset();
    }

    /**
     * Returns a newly allocated byte array containing the same bytes as this buffer.
     */
    default byte[] toByteArray() {
        return Arrays.copyOfRange(buffer(), offset(), limit());
    }

    /**
     * Returns a newly allocated {@link IoBuffer} with the same contents.
     */
    default IoBuffer snapshot() {
        return IoBuffers.wrap(toByteArray());
    }
}
