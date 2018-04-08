package com.pon.ents.base.serie.impl;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.serie.Serie;

public class EmptySerie<T> implements Serie<T> {

    private boolean closed;

    @Override
    public void close() {
        checkNotClosed();
        this.closed = true;
    }

    @Override
    @Nullable
    public T next() {
        checkNotClosed();
        return null;
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
