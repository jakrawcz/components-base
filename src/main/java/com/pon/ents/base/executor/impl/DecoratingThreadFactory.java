package com.pon.ents.base.executor.impl;

import java.util.concurrent.ThreadFactory;
import java.util.function.UnaryOperator;

public class DecoratingThreadFactory implements ThreadFactory {

    private final ThreadFactory underlying;
    private final UnaryOperator<Runnable> runnableDecorator;

    public DecoratingThreadFactory(ThreadFactory underlying, UnaryOperator<Runnable> runnableDecorator) {
        this.underlying = underlying;
        this.runnableDecorator = runnableDecorator;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return underlying.newThread(runnableDecorator.apply(runnable));
    }
}
