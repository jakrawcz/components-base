package com.pon.ents.base.io.impl;

import com.pon.ents.base.io.Output;

public class CountingOutput implements Output {

    private final Output underlying;

    private long writtenCount;

    public CountingOutput(Output underlying, long initialWrittenCount) {
        this.underlying = underlying;
        this.writtenCount = initialWrittenCount;
    }

    /**
     * Returns an initial written byte count (given during construction) incremented by the number of bytes that were
     * written to this {@link Output}.
     * <p>
     * It is legal to call this method after {@link #close() closing}.
     */
    public long writtenCount() {
        return writtenCount;
    }

    @Override
    public void write(byte[] buffer, int offset, int length) {
        underlying.write(buffer, offset, length);
        this.writtenCount += length;
    }

    @Override
    public void close() {
        underlying.close();
    }

    @Override
    public void write(int b) {
        underlying.write(b);
        ++this.writtenCount;
    }
}
