package com.pon.ents.base.io.impl;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;
import com.pon.ents.base.io.Input;

public class ByteInput implements Input {

    private static final int ENDED = -1;
    private static final int CLOSED = -2;

    private int b;

    public ByteInput(byte b) {
        this.b = UnsignedBytes.toInt(b);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        checkNotClosed();
        if (length == 0) {
            return 0;
        }
        if (b == ENDED) {
            return -1;
        }
        buffer[offset] = (byte) b;
        this.b = ENDED;
        return 1;
    }

    @Override
    public int read() {
        checkNotClosed();
        int read = b;
        if (read == ENDED) {
            return -1;
        }
        this.b = ENDED;
        return read;
    }

    @Override
    public long remaining() {
        checkNotClosed();
        return b == ENDED ? 0 : 1;
    }

    @Override
    public void close() {
        checkNotClosed();
        this.b = CLOSED;
    }

    private void checkNotClosed() {
        Preconditions.checkState(b != CLOSED, "already closed");
    }
}
