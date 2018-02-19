package com.pon.ents.base.executor;

import java.util.concurrent.ThreadFactory;
import java.util.function.UnaryOperator;

import com.pon.ents.base.executor.impl.DecoratingThreadFactory;

public abstract class ThreadFactories {

    /**
     * Returns a {@link ThreadFactory} that applies the provided {@link UnaryOperator} to any {@link Runnable} passed
     * to the underlying {@link ThreadFactory}.
     */
    public static ThreadFactory decorating(ThreadFactory underlying, UnaryOperator<Runnable> runnableDecorator) {
        return new DecoratingThreadFactory(underlying, runnableDecorator);
    }
}
