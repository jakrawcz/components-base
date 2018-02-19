package com.pon.ents.base.io.impl;

import java.util.Iterator;

import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.proxy.Proxies;

public class CloseRemainingIterator<T extends RuntimeCloseable> implements CloseableIterator<T> {

    private Iterator<T> underlying;

    public CloseRemainingIterator(Iterator<T> underlying) {
        this.underlying = underlying;
    }

    @Override
    public boolean hasNext() {
        return underlying.hasNext();
    }

    @Override
    public T next() {
        return underlying.next();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void close() {
        while (underlying.hasNext()) {
            T runtimeCloseable = underlying.next();
            runtimeCloseable.close();
        }
        this.underlying = Proxies.throwing(Iterator.class, () -> new IllegalStateException("already closed"));
    }
}
