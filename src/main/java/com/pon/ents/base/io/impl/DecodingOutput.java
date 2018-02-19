package com.pon.ents.base.io.impl;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.Codec;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.Outputs;
import com.pon.ents.base.io.ProducingOutput;

public class DecodingOutput<T> implements ProducingOutput<T> {

    private final Codec<T> codec;
    private final ProducingOutput<IoBuffer> delegate;

    public DecodingOutput(Codec<T> codec) {
        this.codec = codec;
        this.delegate = Outputs.producingIoBuffer();
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
        delegate.close();
    }

    @Override
    public T produce() {
        Input input = Inputs.fromIoBuffer(delegate.produce());
        T object = codec.decode(input);
        int subsequentRead = input.read();
        Preconditions.checkState(subsequentRead == -1,
                "more bytes than required (for decoding a single object) were written");
        return object;
    }
}
