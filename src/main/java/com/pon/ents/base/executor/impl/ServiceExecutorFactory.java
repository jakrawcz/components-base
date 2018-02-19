package com.pon.ents.base.executor.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

import com.pon.ents.base.executor.CloseableExecutor;
import com.pon.ents.base.executor.CloseableExecutors;
import com.pon.ents.base.executor.ExecutorFactory;

public class ServiceExecutorFactory implements ExecutorFactory {

    private final Function<ThreadFactory, ExecutorService> executorServiceFactory;

    public ServiceExecutorFactory(Function<ThreadFactory, ExecutorService> executorServiceFactory) {
        this.executorServiceFactory = executorServiceFactory;
    }

    @Override
    public CloseableExecutor create(ThreadFactory threadFactory) {
        ExecutorService executorService = executorServiceFactory.apply(threadFactory);
        return CloseableExecutors.adapt(executorService);
    }
}
