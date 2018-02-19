package com.pon.ents.base.context.impl;

import java.util.concurrent.ThreadFactory;

import com.pon.ents.base.closeable.Opener;
import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.context.ContextExecutor;
import com.pon.ents.base.context.ContextRunnable;
import com.pon.ents.base.executor.CloseableExecutor;
import com.pon.ents.base.executor.ExecutorFactory;
import com.pon.ents.base.executor.ThreadFactories;

public class ManagedContextExecutor<T extends RuntimeCloseable> implements ContextExecutor<T> {

    private final CloseableExecutor executor;
    private final Opener<T> contextOpener;
    private final ThreadLocal<T> context;

    public ManagedContextExecutor(
            ExecutorFactory executorFactory,
            ThreadFactory threadFactory,
            Opener<T> contextOpener) {
        this.contextOpener = contextOpener;
        this.context = new ThreadLocal<>();
        this.executor = executorFactory.create(ThreadFactories.decorating(threadFactory, ContextManagingRunnable::new));
    }

    @Override
    public void execute(ContextRunnable<T> contextRunnable) {
        executor.execute(new ContextUsingRunnable(contextRunnable));
    }

    @Override
    public void close() {
        executor.close();
    }

    private class ContextUsingRunnable implements Runnable {

        private final ContextRunnable<T> contextRunnable;

        public ContextUsingRunnable(ContextRunnable<T> contextRunnable) {
            this.contextRunnable = contextRunnable;
        }

        @Override
        public void run() {
            contextRunnable.runIn(context.get());
        }
    }

    private class ContextManagingRunnable implements Runnable {

        private final Runnable underlying;

        public ContextManagingRunnable(Runnable underlying) {
            this.underlying = underlying;
        }

        @Override
        public void run() {
            T threadContext = contextOpener.open();
            context.set(threadContext);
            try {
                underlying.run();
            } finally {
                context.remove();
                threadContext.close();
            }
        }
    }
}