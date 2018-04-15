package com.pon.ents.base.io.impl;

import javax.annotation.Nullable;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.serie.Serie;

public class InputIoBufferSerie implements Serie<IoBuffer> {

    private final Input input;
    private final IoBuffer ioBuffer;

    public InputIoBufferSerie(Input input, IoBuffer ioBuffer) {
        this.input = input;
        this.ioBuffer = ioBuffer;
    }

    @Override
    @Nullable
    public IoBuffer next() {
        byte[] buffer = ioBuffer.buffer();
        int offset = ioBuffer.offset();
        int limit = ioBuffer.limit();
        int at = offset;
        while (at < limit) {
            int read = input.read(buffer, at, limit - at);
            if (read == -1) {
                if (offset == at) {
                    return null;
                } else {
                    return ioBuffer.rePositioned(offset, at);
                }
            }
            at += read;
        }
        return ioBuffer;
    }

    @Override
    public long remaining() {
        long remainingByteCount = input.remaining();
        if (remainingByteCount == -1) {
            return -1;
        }
        int bufferLength = ioBuffer.length();;
        return (remainingByteCount + bufferLength - 1) / bufferLength;
    }

    @Override
    public void close() {
        input.close();
    }
}
