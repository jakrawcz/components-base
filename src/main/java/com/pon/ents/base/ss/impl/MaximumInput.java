package com.pon.ents.base.ss.impl;

import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;
import com.pon.ents.base.io.Input;

public class MaximumInput implements Input {

    private boolean closed;

    public MaximumInput() {
        this.closed = false;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        Arrays.fill(buffer, offset, offset + length, UnsignedBytes.MAX_VALUE);
        return length;
    }

    @Override
    public int read() {
        return 255;
    }

    @Override
    public long remaining() {
        return -1;
    }

    @Override
    public void close() {
        Preconditions.checkState(!closed, "already closed");
        this.closed = true;
    }
}
