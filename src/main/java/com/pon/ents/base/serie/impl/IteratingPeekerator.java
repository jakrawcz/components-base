package com.pon.ents.base.serie.impl;

import java.util.Iterator;

import com.pon.ents.base.serie.Peekerator;

public class IteratingPeekerator<T> implements Peekerator<T> {

    private final Iterator<T> rest;

    private T current;

    public IteratingPeekerator(T current, Iterator<T> rest) {
        this.current = current;
        this.rest = rest;
    }

    @Override
    public T current() {
        return current;
    }

    @Override
    public boolean advance() {
        if (!rest.hasNext()) {
            return false;
        }
        this.current = rest.next();
        return true;
    }
}
