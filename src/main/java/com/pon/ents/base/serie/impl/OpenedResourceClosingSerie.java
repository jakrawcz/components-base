package com.pon.ents.base.serie.impl;

import javax.annotation.Nullable;

import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.closeable.RuntimeCloseables;
import com.pon.ents.base.serie.Serie;

public class OpenedResourceClosingSerie<T extends RuntimeCloseable> implements Serie<T> {

    private Serie<T> underlying;

    public OpenedResourceClosingSerie(Serie<T> underlying) {
        this.underlying = underlying;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void close() {
        while (true) {
            @Nullable T resource = underlying.next();
            if (resource == null) {
                break;
            }
            resource.close();
        }
        this.underlying = RuntimeCloseables.closed((Class<Serie<T>>) (Object) Serie.class);
    }

    @Override
    public T next() {
        return underlying.next();
    }

    @Override
    public long remaining() {
        return underlying.remaining();
    }
}
