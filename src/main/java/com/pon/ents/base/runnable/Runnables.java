package com.pon.ents.base.runnable;

import com.pon.ents.base.runnable.impl.IdempotentRunnable;
import com.pon.ents.base.runnable.impl.OnlyOnceRunnable;

public abstract class Runnables {

    private static final Runnable NOOP = () -> {};

    /**
     * Returns a wrapper that will execute the given {@link Runnable} only once (thread-safely), ignoring any other
     * {@link Runnable#run()} calls.
     */
    public static Runnable idempotent(Runnable runnable) {
        return new IdempotentRunnable(runnable);
    }

    /**
     * Returns a wrapper that will execute the given {@link Runnable} only once (thread-safely).
     * <p>
     * It is illegal to {@link Runnable#run()} it again.
     */
    public static Runnable onlyOnce(Runnable runnable) {
        return new OnlyOnceRunnable(runnable);
    }

    /**
     * Returns a {@link Runnable} that will do nothing. Every time.
     */
    public static Runnable noop() {
        return NOOP;
    }
}
