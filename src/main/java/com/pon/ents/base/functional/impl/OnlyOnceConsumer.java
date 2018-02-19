package com.pon.ents.base.functional.impl;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class OnlyOnceConsumer<T> implements Consumer<T> {

    private final AtomicReference<Consumer<T>> underlying;

    public OnlyOnceConsumer(Consumer<T> underlying) {
        this.underlying = new AtomicReference<>(Preconditions.checkNotNull(underlying));
    }

    @Override
    public void accept(T t) {
        @Nullable Consumer<T> currentConsumer = underlying.getAndSet(null);
        Preconditions.checkState(currentConsumer != null);
        currentConsumer.accept(t);
    }
}
