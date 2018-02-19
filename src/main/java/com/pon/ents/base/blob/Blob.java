package com.pon.ents.base.blob;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Output;

/**
 * A {@link #read(long) readable}/{@link #write(long) writable} sequence of bytes.
 * <p>
 * Every {@code relativeOffset} parameter is interpreted the following way:
 * <ul>
 *     <li>when {@code relativeOffset >= 0} - denotes a number of bytes to skip from the beginning of the blob
 *         (a "natural" meaning of an offset);</li>
 *     <li>otherwise (when {@code relativeOffset < 0} - denotes that {@code length() + 1 + relativeOffset} bytes should
 *         be skipped (in other words: the position should be {@code 1 - relativeOffset} bytes counting from the end);
 *         in particular, a {@code relativeOffset} of -1 positions the cursor at the current end of the blob.
 * </ul>
 */
public interface Blob {

    /**
     * Returns an {@link Input} containing the bytes of this blob read from the given {@code relativeOffset}.
     * <p>
     * Please see the class documentation for a {@code relativeOffset} contract.
     */
    Input read(long relativeOffset);

    /**
     * Truncates this blob at the given {@code relativeOffset} and returns an {@link Output} allowing to overwrite all
     * the subsequent bytes (and, optionally, append additional ones).
     * <p>
     * Please see the class documentation for a {@code relativeOffset} contract.
     */
    Output write(long relativeOffset);

    /**
     * Returns the current number of bytes contained in the blob.
     * <p>
     * This is an optional operation.
     */
    default long length() {
        try (Input input = read(0)) {
            long remaining = input.remaining();
            if (remaining == -1) {
                throw new UnsupportedOperationException();
            }
            return remaining;
        }
    }

    /**
     * {@link #read(long) Reads} from the beginning.
     */
    default Input read() {
        return read(0);
    }

    /**
     * {@link #write(long) Writes} from the beginning.
     */
    default Output write() {
        return write(0);
    }

    /**
     * {@link #read(long) Reads} the given number of bytes located at the end.
     */
    default Input readLast(long length) {
        return read(-1 - length);
    }

    /**
     * {@link #write(long) Writes} at the ending.
     */
    default Output append() {
        return write(-1);
    }

    /**
     * Makes the contents of this {@link Blob} empty.
     */
    default void clear() {
        truncateTo(0);
    }

    /**
     * Truncates this blob at the given {@code relativeOffset}.
     */
    default void truncateTo(long relativeOffset) {
        write(relativeOffset).close();
    }

    /**
     * Returns true if {@link #length()} is 0.
     */
    default boolean isEmpty() {
        return length() == 0;
    }
}
