package com.pon.ents.base.closeable.impl;

import java.util.Iterator;

import com.pon.ents.base.closeable.RuntimeCloseable;

public class CompositeRuntimeCloseable implements RuntimeCloseable {

    private final Iterator<? extends RuntimeCloseable> runtimeCloseables;

    public CompositeRuntimeCloseable(Iterator<? extends RuntimeCloseable> runtimeCloseables) {
        this.runtimeCloseables = runtimeCloseables;
    }

    @Override
    public void close() {
        while (runtimeCloseables.hasNext()) {
            RuntimeCloseable runtimeCloseable = runtimeCloseables.next();
            runtimeCloseable.close();
        }
    }
}
