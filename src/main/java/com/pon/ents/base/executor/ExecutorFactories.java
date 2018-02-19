package com.pon.ents.base.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

import com.pon.ents.base.closeable.Opener;
import com.pon.ents.base.executor.impl.DynamicPoolExecutorFactory;
import com.pon.ents.base.executor.impl.ServiceExecutorFactory;
import com.pon.ents.base.listenable.Listenable;
import com.pon.ents.base.queue.CloseableQueue;

public abstract class ExecutorFactories {

    /**
     * Returns a {@link ExecutorFactory factory} of a dynamically sized thread pool, maintaining the number of threads
     * given by the {@link Listenable} int, using the {@link CloseableQueue} implementation given by the {@link Opener}.
     */
    public static ExecutorFactory dynamicPool(
            Opener<CloseableQueue<Runnable>> queueOpener,
            Listenable<Integer> threadCount) {
        return new DynamicPoolExecutorFactory(queueOpener, threadCount);
    }

    /**
     * Returns an {@link ExecutorFactory} wrapping a more usual {@link ExecutorService} factory.
     */
    public static ExecutorFactory adapt(Function<ThreadFactory, ExecutorService> executorServiceFactory) {
        return new ServiceExecutorFactory(executorServiceFactory);
    }
}
