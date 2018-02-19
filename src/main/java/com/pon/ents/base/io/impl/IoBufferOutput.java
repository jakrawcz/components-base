package com.pon.ents.base.io.impl;

import java.util.function.Consumer;

import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.Output;

public class IoBufferOutput implements Output {

    private final IoBufferProducingOutput delegate;
    private final Consumer<IoBuffer> onCloseResultConsumer;

    public IoBufferOutput(Consumer<IoBuffer> onCloseResultConsumer) {
        this(new IoBufferBuilder(), onCloseResultConsumer);
    }

    public IoBufferOutput(IoBufferBuilder builder, Consumer<IoBuffer> onCloseResultConsumer) {
        this.delegate = new IoBufferProducingOutput(builder);
        this.onCloseResultConsumer = onCloseResultConsumer;
    }

    @Override
    public void write(byte[] buffer, int offset, int length) {
        delegate.write(buffer, offset, length);
    }

    @Override
    public void write(int b) {
        delegate.write(b);
    }

    @Override
    public void close() {
        IoBuffer ioBuffer = delegate.produce();
        onCloseResultConsumer.accept(ioBuffer);
    }
}
