package com.pon.ents.base.serie.impl;

import javax.annotation.Nullable;

import com.google.common.collect.AbstractIterator;
import com.pon.ents.base.serie.Serie;

public class SerieIterator<T> extends AbstractIterator<T> {

    private final Serie<T> underlying;

    public SerieIterator(Serie<T> underlying) {
        this.underlying = underlying;
    }

    @Override
    protected T computeNext() {
        @Nullable T next = underlying.next();
        return next == null ? endOfData() : next;
    }
}
