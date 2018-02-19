package com.pon.ents.base.io.impl;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.Input;

public class EmptyInput implements Input {

    private boolean closed;

    public EmptyInput() {
        this.closed = false;
    }

    @Override
    public void close() {
        checkNotClosed();
        this.closed = true;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        checkNotClosed();
        return -1;
    }

    @Override
    public long remaining() {
        checkNotClosed();
        return 0;
    }

    private void checkNotClosed() {
        Preconditions.checkState(!closed, "already closed");
    }
}
