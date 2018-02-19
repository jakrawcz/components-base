package com.pon.ents.base.executor;

import java.util.concurrent.ThreadFactory;

/**
 * A factory of {@link CloseableExecutor}s.
 */
public interface ExecutorFactory {

    /**
     * Returns a new {@link CloseableExecutor} that will
     */
    CloseableExecutor create(ThreadFactory threadFactory);
}
