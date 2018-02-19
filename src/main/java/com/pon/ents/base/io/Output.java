package com.pon.ents.base.io;

import java.io.IOException;
import java.io.OutputStream;

import com.pon.ents.base.closeable.RuntimeCloseable;

/**
 * An {@link OutputStream} counterpart without optional APIs and checked {@link IOException}s.
 */
public interface Output extends RuntimeCloseable {

    /**
     * Follows the {@link OutputStream#write(byte[], int, int)} contract.
     */
    void write(byte[] buffer, int offset, int length);

    /**
     * Performs {@link #write(byte[], int, int)} with the {@link IoBuffer}.
     */
    default void write(IoBuffer ioBuffer) {
        write(ioBuffer.buffer(), ioBuffer.offset(), ioBuffer.limit());
    }

    /**
     * Performs a {@link #write(byte[], int, int)} with a default offset (0) and length (all).
     */
    default void write(byte buffer[]) {
        write(buffer, 0, buffer.length);
    }

    /**
     * Follows the {@link OutputStream#write(int)} contract.
     */
    default void write(int b) {
        write(new byte[] {(byte) b});
    }

    /**
     * Writes a single byte.
     */
    default void writeByte(byte b) {
        write(b);
    }

    /**
     * Writes {@value Long#BYTES} bytes.
     */
    default void writeLong(long v) {
        writeByte((byte) (v >>> 56));
        writeByte((byte) (v >>> 48));
        writeByte((byte) (v >>> 40));
        writeByte((byte) (v >>> 32));
        writeByte((byte) (v >>> 24));
        writeByte((byte) (v >>> 16));
        writeByte((byte) (v >>> 8));
        writeByte((byte) (v >>> 0));
    }

    /**
     * Writes {@value Integer#BYTES} bytes.
     */
    default void writeInt(int v) {
        writeByte((byte) (v >>> 24));
        writeByte((byte) (v >>> 16));
        writeByte((byte) (v >>> 8));
        writeByte((byte) (v >>> 0));
    }

    /**
     * Writes {@value Double#BYTES} bytes.
     */
    default void writeDouble(double v) {
        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes {@value Float#BYTES} bytes.
     */
    default void writeFloat(float v) {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Writes {@value Short#BYTES} bytes.
     */
    default void writeShort(short v) {
        writeByte((byte) (v >>> 8));
        writeByte((byte) (v >>> 0));
    }

    /**
     * Writes {@value Character#BYTES} bytes.
     */
    default void writeChar(char v) {
        writeByte((byte) (v >>> 8));
        writeByte((byte) (v >>> 0));
    }
}
