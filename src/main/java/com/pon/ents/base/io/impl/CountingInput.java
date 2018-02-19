package com.pon.ents.base.io.impl;

import com.pon.ents.base.io.Input;

public class CountingInput implements Input {

    private final Input underlying;

    private long readCount;

    public CountingInput(Input underlying, long initialReadCount) {
        this.underlying = underlying;
        this.readCount = initialReadCount;
    }

    /**
     * Returns an initial read byte count (given during construction) incremented by the number of bytes that were read
     * from this {@link Input}.
     * <p>
     * It is legal to call this method after {@link #close() closing}.
     */
    public long readCount() {
        return readCount;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        int read = underlying.read(buffer, offset, length);
        if (read != -1) {
            this.readCount += read;
        }
        return read;
    }

    @Override
    public long remaining() {
        return underlying.remaining();
    }

    @Override
    public void close() {
        underlying.close();
    }

    @Override
    public int read() {
        int read = underlying.read();
        if (read != -1) {
            ++this.readCount;
        }
        return read;
    }
}
