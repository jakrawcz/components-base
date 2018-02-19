package com.pon.ents.base.executor.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import com.pon.ents.base.executor.CloseableExecutor;
import com.pon.ents.base.executor.ex.WaitInterruptedException;

public class ServiceCloseableExecutor implements CloseableExecutor {

    private final ExecutorService executorService;

    public ServiceCloseableExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void execute(Runnable runnable) {
        try {
            executorService.execute(runnable);
        } catch (RejectedExecutionException e) {
            throw new IllegalStateException("closed", e);
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new WaitInterruptedException(e);
        }
    }
}
