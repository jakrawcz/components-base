package com.pon.ents.base.io;

import java.util.function.Consumer;

import com.pon.ents.base.io.impl.IoBufferOutput;
import com.pon.ents.base.io.impl.IoBufferProducingOutput;

public abstract class Outputs {

    /**
     * Returns an {@link Output} that will collect all written bytes in an internally grown {@link IoBuffer} and will
     * pass it to the given {@link Consumer} when closed.
     */
    public static Output toIoBuffer(Consumer<IoBuffer> onCloseResultConsumer) {
        return new IoBufferOutput(onCloseResultConsumer);
    }

    /**
     * Returns an {@link ProducingOutput output} producing an {@link IoBuffer} with all the buffered bytes.
     */
    public static ProducingOutput<IoBuffer> producingIoBuffer() {
        return new IoBufferProducingOutput();
    }
}
