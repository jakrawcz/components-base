package com.pon.ents.base.context;

import java.util.concurrent.Executor;

import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.executor.CloseableExecutor;

/**
 * A counterpart of a {@link CloseableExecutor} that executes {@link ContextRunnable}s (instead of context-free
 * {@link Runnable}s).
 * <p>
 * Please see the {@link CloseableExecutor}'s contract regarding the {@link RuntimeCloseable#close} operation.
 */
public interface ContextExecutor<T> extends RuntimeCloseable {

    /**
     * Executes the given {@link ContextRunnable context-aware runnable} at some time in the future, giving it some
     * instance of a required context (implementation-specific).
     * <p>
     * Similarly to the regular {@link Executor}, the command may execute in a new thread, in a pooled thread, or in the
     * calling thread, at the discretion of the {@code ContextExecutor} implementation.
     */
    void execute(ContextRunnable<T> contextRunnable);
}
