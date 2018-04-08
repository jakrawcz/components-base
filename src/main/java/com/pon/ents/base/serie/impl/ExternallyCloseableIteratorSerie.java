package com.pon.ents.base.serie.impl;

import java.util.Iterator;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.runnable.Runnables;
import com.pon.ents.base.serie.Serie;

public class ExternallyCloseableIteratorSerie<T> implements Serie<T> {

    private final Iterator<T> iterator;

    @Nullable
    private Runnable closingTask;

    private long remaining;

    public ExternallyCloseableIteratorSerie(Iterator<T> iterator, long remaining, Runnable closingTask) {
        Preconditions.checkArgument(remaining >= -1, "illegal remaining: %s", remaining);
        this.iterator = iterator;
        this.remaining = remaining;
        this.closingTask = Preconditions.checkNotNull(closingTask);
    }

    @Override
    public void close() {
        checkNotClosed();
        closingTask.run();
        this.closingTask = null;
    }

    @Override
    @Nullable
    public T next() {
        checkNotClosed();
        if (!iterator.hasNext()) {
            Preconditions.checkState(remaining <= 0, "remaining contract violation: %s missing elements", remaining);
            closingTask.run();
            this.closingTask = Runnables.noop();
            return null;
        }
        T element = iterator.next();
        Preconditions.checkNotNull(element, "underlying iterator contract violation");
        if (remaining != -1) {
            Preconditions.checkState(remaining > 0, "remaining contract violation: at least one superfluous element");
            --this.remaining;
        }
        return element;
    }

    @Override
    public long remaining() {
        checkNotClosed();
        return remaining;
    }

    private void checkNotClosed() {
        Preconditions.checkState(closingTask != null, "already closed");
    }
}
