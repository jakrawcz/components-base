package com.pon.ents.base.context;

import java.util.concurrent.ThreadFactory;

import com.pon.ents.base.closeable.Opener;
import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.context.impl.ManagedContextExecutor;
import com.pon.ents.base.executor.ExecutorFactory;

public abstract class ContextExecutors {

    /**
     * TODO: document
     */
    public static <T extends RuntimeCloseable> ContextExecutor<T> onExecutor(
            ExecutorFactory executorFactory, ThreadFactory threadFactory, Opener<T> contextOpener) {
        return new ManagedContextExecutor<>(executorFactory, threadFactory, contextOpener);
    }
}
