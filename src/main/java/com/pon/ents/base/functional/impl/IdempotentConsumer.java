package com.pon.ents.base.functional.impl;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class IdempotentConsumer<T> implements Consumer<T> {

    private final AtomicReference<Consumer<T>> underlying;

    public IdempotentConsumer(Consumer<T> underlying) {
        this.underlying = new AtomicReference<>(Preconditions.checkNotNull(underlying));
    }

    @Override
    public void accept(T t) {
        @Nullable Consumer<T> currentConsumer = underlying.getAndSet(null);
        if (currentConsumer == null) {
            return;
        }
        currentConsumer.accept(t);
    }
}
