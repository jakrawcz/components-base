package com.pon.ents.base.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import com.google.common.primitives.Chars;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.io.ex.RuntimeEofException;
import com.pon.ents.base.io.ex.RuntimeIoException;

/**
 * An {@link InputStream} counterpart with more formal {@link #remaining()} contract, but without optional APIs and
 * checked {@link IOException}s.
 * <p>
 * This interface is {@link RuntimeCloseable}, but needs {@link RuntimeCloseable#close() closing} only in the situations
 * where it is not read until the end (i.e. until any read operation returns {@literal -1}) and where no
 * {@link RuntimeIoException} is thrown. If these happen, the {@link Input} is assumed to have released its resources
 * anyway, and {@link #close()} is legal, but no-op.
 * <p>
 * For every read/skip method, the implementation is guaranteed to consume and parse exactly the number of bytes
 * declared by the method's contract (e.g. it is illegal to implement {@link #readLong()} as a "varint"; or in other
 * words: performing a {@link #readByte()} 8 times and assembling a long must give the same result as calling the
 * {@link #readLong()} once).
 * <p>
 * Additionally, if a method specifies that it precisely "reads X bytes", then in throws a {@link RuntimeEofException}
 * if less bytes are available (similarly to {@link DataInput}'s contract).
 */
public interface Input extends RuntimeCloseable {

    /**
     * Follows the {@link InputStream#read(byte[], int, int)} contract.
     */
    int read(byte[] buffer, int offset, int length);

    /**
     * Performs {@link #read(byte[], int, int)} with the unwrapped {@link IoBuffer}.
     */
    default int read(IoBuffer ioBuffer) {
        return read(ioBuffer.buffer(), ioBuffer.offset(), ioBuffer.limit());
    }

    /**
     * Performs a {@link #read(byte[], int, int)} with a default offset (0) and length (all).
     */
    default int read(byte buffer[]) {
        return read(buffer, 0, buffer.length);
    }

    /**
     * Follows the {@link InputStream#read()} contract.
     */
    default int read() {
        byte[] buffer = new byte[1];
        int read = read(buffer);
        if (read == -1) {
            return -1;
        }
        return buffer[0];
    }

    /**
     * Returns an exact number of remaining bytes, or -1 if unknown (or e.g. infinite).
     * <p>
     * If at any point this method returns an exact number, then all future calls will return the exact number as well
     * (and precisely the one that is smaller from the previously returned by the number of bytes read since then; in
     * other words: the implementation will track the known value accurately and will never turn it "unknown").
     */
    long remaining();

    /**
     * Returns a remaining {@link Size}, or {@literal null} if unknown.
     * <p>
     * This is a slower, object-oriented counterpart of the {@link #remaining()}.
     */
    @Nullable
    default Size remainingSize() {
        long remainingBytes = remaining();
        return remainingBytes == -1 ? null : Sizes.bytes(remainingBytes);
    }

    /**
     * Reads {@code length} bytes into the given buffer at the given offset.
     */
    default void readFully(byte[] buffer, int offset, int length) {
        int at = 0;
        while (at < length) {
            int read = read(buffer, offset + at, length - at);
            if (read == -1) {
                throw new RuntimeEofException();
            }
            at += read;
        }
    }

    /**
     * Performs a {@link #readFully(byte[], int, int)} with a default offset (0) and length (all).
     */
    default void readFully(byte[] buffer) {
        readFully(buffer, 0, buffer.length);
    }

    /**
     * Skips at least 1, and up to {@code count} bytes from the input and returns the skipped count (this includes
     * returning 0 for the requested 0 count), or returns -1 if nothing can be skipped (i.e. when at the end of input).
     */
    default long skip(long count) {
        if (count == 0) {
            return 0;
        }
        byte[] buffer = new byte[(int) Math.min(4096, count)];
        long toSkip = count;
        while (toSkip > 0) {
            int skipped = read(buffer, 0, (int) Math.min(buffer.length, toSkip));
            if (skipped == -1) {
                break;
            }
            toSkip -= skipped;
        }
        return count - toSkip;
    }

    /**
     * Skips {@code count} bytes.
     */
    default void skipFully(long count) {
        long toSkip = count;
        while (toSkip > 0) {
            long skipped = skip(count);
            if (skipped == -1) {
                throw new RuntimeEofException("was able to only skip " + (count - toSkip) + "/" + count + " bytes");
            }
            toSkip -= skipped;
        }
    }

    /**
     * Reads a single byte.
     */
    default byte readByte() {
        int read = read();
        if (read == -1) {
            throw new RuntimeEofException();
        }
        return (byte) read;
    }

    /**
     * Reads {@value Long#BYTES} bytes and returns them as a long.
     */
    default long readLong() {
        return Longs.fromBytes(readByte(), readByte(), readByte(), readByte(),
                readByte(), readByte(), readByte(), readByte());
    }

    /**
     * Reads {@value Integer#BYTES} bytes and returns them as an int.
     */
    default int readInt() {
        return Ints.fromBytes(readByte(), readByte(), readByte(), readByte());
    }

    /**
     * Reads {@value Double#BYTES} bytes and returns them as a double.
     */
    default double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    /**
     * Reads {@value Float#BYTES} bytes and returns them as a float.
     */
    default float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads {@value Short#BYTES} bytes and returns them as a short.
     */
    default short readShort() {
        return Shorts.fromBytes(readByte(), readByte());
    }

    /**
     * Reads {@value Character#BYTES} bytes and returns them as a char.
     */
    default char readChar() {
        return Chars.fromBytes(readByte(), readByte());
    }
}
