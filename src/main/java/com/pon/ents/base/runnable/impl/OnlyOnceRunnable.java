package com.pon.ents.base.runnable.impl;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class OnlyOnceRunnable implements Runnable {

    private final AtomicReference<Runnable> underlying;

    public OnlyOnceRunnable(Runnable underlying) {
        this.underlying = new AtomicReference<>(Preconditions.checkNotNull(underlying));
    }

    @Override
    public void run() {
        @Nullable Runnable currentRunnable = underlying.getAndSet(null);
        Preconditions.checkState(currentRunnable != null);
        currentRunnable.run();
    }
}
