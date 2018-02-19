package com.pon.ents.base.closeable.impl;

import java.util.Iterator;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.closeable.CloseableIterator;

public class CloseableIteratorAdapter<T> implements CloseableIterator<T> {

    private final Iterator<T> iterator;

    @Nullable
    private Runnable closingTask;

    public CloseableIteratorAdapter(Iterator<T> iterator, Runnable closingTask) {
        this.iterator = iterator;
        this.closingTask = closingTask;
    }

    @Override
    public T next() {
        checkNotClosed();
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        checkNotClosed();
        return iterator.hasNext();
    }

    @Override
    public void close() {
        checkNotClosed();
        closingTask.run();
        this.closingTask = null;
    }

    private void checkNotClosed() {
        Preconditions.checkState(closingTask != null, "already closed");
    }
}
