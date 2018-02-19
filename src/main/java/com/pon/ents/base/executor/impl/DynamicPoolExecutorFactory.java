package com.pon.ents.base.executor.impl;

import java.util.concurrent.ThreadFactory;

import com.pon.ents.base.closeable.Opener;
import com.pon.ents.base.executor.CloseableExecutor;
import com.pon.ents.base.executor.ExecutorFactory;
import com.pon.ents.base.listenable.Listenable;
import com.pon.ents.base.queue.CloseableQueue;

public class DynamicPoolExecutorFactory implements ExecutorFactory {

    private final Opener<CloseableQueue<Runnable>> queueOpener;
    private final Listenable<Integer> threadCount;

    public DynamicPoolExecutorFactory(
            Opener<CloseableQueue<Runnable>> queueOpener,
            Listenable<Integer> threadCount) {
        this.queueOpener = queueOpener;
        this.threadCount = threadCount;
    }

    @Override
    public CloseableExecutor create(ThreadFactory threadFactory) {
        return new DynamicPoolExecutor(queueOpener, threadFactory, threadCount);
    }
}
