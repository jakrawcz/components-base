package com.pon.ents.base.io;

import java.util.Arrays;

import com.google.common.primitives.UnsignedBytes;

public abstract class IoBuffers {

    /**
     * Constructs an {@link IoBuffer} directly.
     */
    public static IoBuffer wrap(byte[] buffer, int offset, int limit) {
        return new Direct(buffer, offset, limit);
    }

    /**
     * {@link #wrap(byte[], int, int) Wraps} a full-length buffer.
     */
    public static IoBuffer wrap(byte[] buffer, int offset) {
        return wrap(buffer, offset, buffer.length);
    }

    /**
     * {@link #wrap(byte[], int) Wraps} a zero-offset buffer.
     */
    public static IoBuffer wrap(byte[] buffer) {
        return wrap(buffer, 0);
    }

    /**
     * A {@link #wrap(byte[])} alias with var-args syntax.
     */
    public static IoBuffer of(byte... bytes) {
        return wrap(bytes);
    }

    /**
     * Returns an {@link IoBuffer} containing the given unsigned byte values.
     */
    public static IoBuffer of(int... unsignedBytes) {
        byte[] bytes = new byte[unsignedBytes.length];
        for (int i = 0; i < unsignedBytes.length; ++i) {
            bytes[i] = UnsignedBytes.checkedCast(unsignedBytes[i]);
        }
        return wrap(bytes);
    }

    /**
     * Returns an empty {@link IoBuffer}.
     */
    public static IoBuffer empty() {
        return Empty.INSTANCE;
    }

    /**
     * Returns a hash code identical to what {@link Arrays#hashCode(byte[])} would return for a byte array holding the
     * same bytes as the given {@link IoBuffer}.
     */
    public static int hashCodeOf(IoBuffer ioBuffer) {
        int result = 1;
        int offset = ioBuffer.offset();
        int limit = ioBuffer.limit();
        byte[] buffer = ioBuffer.buffer();
        for (int i = offset; i < limit; ++i) {
            result = 31 * result + buffer[i];
        }
        return result;
    }

    /**
     * A byte-by-byte {@link IoBuffer#equals(Object)} implementation.
     */
    public static boolean equal(IoBuffer ioBuffer, Object object) {
        if (ioBuffer == object) {
            return true;
        }
        if (!(object instanceof IoBuffer)) {
            return false;
        }
        IoBuffer other = (IoBuffer) object;
        int offset = ioBuffer.offset();
        int limit = ioBuffer.limit();
        int otherOffset = other.offset();
        int otherLimit = other.limit();
        int length = limit - offset;
        if (otherLimit - otherOffset != length) {
            return false;
        }
        byte[] buffer = ioBuffer.buffer();
        byte[] otherBuffer = other.buffer();
        if (buffer == otherBuffer) {
            return offset == otherOffset;
        }
        for (int i = 0; i < length; ++i) {
            if (buffer[offset + i] != otherBuffer[otherOffset + i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * An {@link IoBuffer#toString()} implementation.
     */
    public static String toString(IoBuffer ioBuffer) {
        byte[] buffer = ioBuffer.buffer();
        int offset = ioBuffer.offset();
        int limit = ioBuffer.limit();
        StringBuilder render = new StringBuilder(ioBuffer.length() * 4);
        render.append('[');
        for (int i = offset; i < limit; ++i) {
            render.append(buffer[i]);
            render.append(',');
            render.append(' ');
        }
        if (render.length() > 1) {
            render.setLength(render.length() - 1);
            render.setCharAt(render.length() - 1, ']');
        }
        return IoBuffer.class.getSimpleName()
                + "(byte[" + buffer.length + "][" + offset + "; " + limit + "): " + render + ")";
    }

    /**
     * Returns a length of common prefix of the two {@link IoBuffer}s.
     */
    public static int commonPrefixLength(IoBuffer first, IoBuffer second) {
        byte[] firstBuffer = first.buffer();
        int firstOffset = first.offset();
        int firstLength = first.limit() - firstOffset;
        byte[] secondBuffer = second.buffer();
        int secondOffset = second.offset();
        int secondLength = second.limit() - secondOffset;
        int minimumLength = Math.min(firstLength, secondLength);
        for (int i = 0; i < minimumLength; ++i) {
            if (firstBuffer[firstOffset + i] != secondBuffer[secondOffset + i]) {
                return i;
            }
        }
        return minimumLength;
    }

    private static class Empty implements IoBuffer {

        public static final Empty INSTANCE = new Empty();

        private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

        @Override
        public byte[] buffer() {
            return EMPTY_BYTE_ARRAY;
        }

        @Override
        public int offset() {
            return 0;
        }

        @Override
        public int limit() {
            return 0;
        }

        @Override
        public int hashCode() {
            return IoBuffers.hashCodeOf(this);
        }

        @Override
        public boolean equals(Object object) {
            return IoBuffers.equal(this, object);
        }

        @Override
        public String toString() {
            return IoBuffers.toString(this);
        }
    }

    private static class Direct implements IoBuffer {

        private final byte[] buffer;
        private final int offset;
        private final int limit;

        public Direct(byte[] buffer, int offset, int limit) {
            this.buffer = buffer;
            this.offset = offset;
            this.limit = limit;
        }

        @Override
        public byte[] buffer() {
            return buffer;
        }

        @Override
        public int offset() {
            return offset;
        }

        @Override
        public int limit() {
            return limit;
        }

        @Override
        public int hashCode() {
            return IoBuffers.hashCodeOf(this);
        }

        @Override
        public boolean equals(Object object) {
            return IoBuffers.equal(this, object);
        }

        @Override
        public String toString() {
            return IoBuffers.toString(this);
        }
    }
}
