package com.pon.ents.base.serie.impl;

import java.util.Comparator;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class AlternatingTopper<T> implements Topper<T> {

    private final Comparator<? super T> comparator;

    @Nullable
    private T first;

    @Nullable
    private T second;

    public AlternatingTopper(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void add(T object) {
        Preconditions.checkNotNull(object);
        if (first == null) {
            this.first = object;
        } else {
            Preconditions.checkState(second == null, "full");
            this.second = object;
        }
    }

    @Override
    public T removeTop() {
        T firstRef = first;
        T secondRef = second;
        if (secondRef == null) {
            this.first = null;
            return firstRef;
        }
        if (firstRef == null) {
            this.second = null;
            return secondRef;
        }
        int c = comparator.compare(firstRef, secondRef);
        if (c < 0) {
            this.first = null;
            return firstRef;
        } else {
            this.second = null;
            return secondRef;
        }
    }
}
