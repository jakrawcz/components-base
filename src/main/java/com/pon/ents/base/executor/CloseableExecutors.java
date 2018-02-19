package com.pon.ents.base.executor;

import java.util.concurrent.ExecutorService;

import com.pon.ents.base.executor.impl.ServiceCloseableExecutor;

public abstract class CloseableExecutors {

    /**
     * Returns a {@link CloseableExecutor} wrapping the given {@link ExecutorService}.
     */
    public static CloseableExecutor adapt(ExecutorService executorService) {
        return new ServiceCloseableExecutor(executorService);
    }
}
