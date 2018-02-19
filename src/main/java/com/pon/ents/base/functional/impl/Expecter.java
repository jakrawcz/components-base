package com.pon.ents.base.functional.impl;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.functional.Exchanger;

public class Expecter<T> implements Exchanger<T> {

    private static final Object UNSET_MARKER = new Object();
    private static final Object GOT_MARKER = new Object();

    private T object;

    @SuppressWarnings("unchecked")
    public Expecter() {
        this.object = (T) UNSET_MARKER;
    }

    @Override
    public void accept(@Nullable T object) {
        Preconditions.checkState(object == UNSET_MARKER, "already set");
        this.object = object;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public T get() {
        Preconditions.checkState(object != UNSET_MARKER, "not set yet");
        Preconditions.checkState(object != GOT_MARKER, "already got");
        @Nullable T set = object;
        this.object = (T) GOT_MARKER;
        return set;
    }

}
