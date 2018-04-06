package com.pon.ents.base.context;

/**
 * A counterpart of a {@link Runnable} that needs some instance of a context (of a specific type) to run.
 * <p>
 * This type of context-aware command is designed to be executed by a {@link ContextExecutor}.
 */
public interface ContextRunnable<T> {

    /**
     * Runs this {@link ContextRunnable} in the given context.
     */
    void runIn(T context);
}
