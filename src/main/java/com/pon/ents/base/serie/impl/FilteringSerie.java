package com.pon.ents.base.serie.impl;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.pon.ents.base.serie.Serie;

public class FilteringSerie<T> implements Serie<T> {

    private final Serie<T> underlying;
    private final Predicate<? super T> predicate;

    public FilteringSerie(Serie<T> underlying, Predicate<? super T> predicate) {
        this.underlying = underlying;
        this.predicate = predicate;
    }

    @Override
    public void close() {
        underlying.close();
    }

    @Override
    @Nullable
    public T next() {
        @Nullable T element = underlying.next();
        if (element == null) {
            return null;
        }
        if (predicate.test(element)) {
            return element;
        } else {
            return null;
        }
    }

    @Override
    public long remaining() {
        return -1;
    }
}
