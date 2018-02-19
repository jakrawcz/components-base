package com.pon.ents.base.runnable.impl;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class IdempotentRunnable implements Runnable {

    private final AtomicReference<Runnable> underlying;

    public IdempotentRunnable(Runnable underlying) {
        this.underlying = new AtomicReference<>(Preconditions.checkNotNull(underlying));
    }

    @Override
    public void run() {
        @Nullable Runnable currentRunnable = underlying.getAndSet(null);
        if (currentRunnable == null) {
            return;
        }
        currentRunnable.run();
    }
}
