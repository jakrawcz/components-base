package com.pon.ents.base.functional.impl;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.functional.Exchanger;

public class ConcurrentExpecter<T> implements Exchanger<T> {

    private static final Object UNSET_MARKER = new Object();
    private static final Object GOT_MARKER = new Object();

    private final AtomicReference<T> objectReference;

    @SuppressWarnings("unchecked")
    public ConcurrentExpecter() {
        this.objectReference = new AtomicReference<>((T) UNSET_MARKER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void accept(@Nullable T object) {
        boolean set = objectReference.compareAndSet((T) UNSET_MARKER, object);
        Preconditions.checkState(set, "already set");
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public T get() {
        return objectReference.getAndUpdate(object -> {
            Preconditions.checkState(object != UNSET_MARKER, "not set yet");
            Preconditions.checkState(object != GOT_MARKER, "already got");
            return (T) GOT_MARKER;
        });
    }
}
