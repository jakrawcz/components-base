package com.pon.ents.base.io.impl;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.IoBuffers;
import com.pon.ents.base.io.ProducingOutput;

public class IoBufferProducingOutput implements ProducingOutput<IoBuffer> {

    private static final IoBufferBuilder CLOSED_MARKER = new IoBufferBuilder(IoBuffers.empty());
    private static final IoBufferBuilder PRODUCED_MARKER = new IoBufferBuilder(IoBuffers.empty());

    @Nullable
    private IoBufferBuilder builder;

    public IoBufferProducingOutput() {
        this(new IoBufferBuilder());
    }

    public IoBufferProducingOutput(IoBufferBuilder builder) {
        this.builder = Preconditions.checkNotNull(builder);
    }

    @Override
    public void write(byte[] buffer, int offset, int length) {
        checkNotClosed();
        int at = builder.declareWrite(length);
        System.arraycopy(buffer, offset, builder.access(), at, length);
    }

    @Override
    public void write(int b) {
        checkNotClosed();
        int at = builder.declareWrite(1);
        builder.access()[at] = (byte) b;
    }

    @Override
    public IoBuffer produce() {
        checkNotClosed();
        IoBuffer ioBuffer = builder.snapshot();
        this.builder = PRODUCED_MARKER;
        return ioBuffer;
    }

    @Override
    public void close() {
        checkNotClosed();
        this.builder = CLOSED_MARKER;
    }

    private void checkNotClosed() {
        Preconditions.checkState(builder != CLOSED_MARKER, "already closed");
        Preconditions.checkState(builder != PRODUCED_MARKER, "already produced");
    }
}
