package com.pon.ents.base.executor.impl;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.pon.ents.base.closeable.Opener;
import com.pon.ents.base.executor.CloseableExecutor;
import com.pon.ents.base.executor.ex.WaitInterruptedException;
import com.pon.ents.base.listenable.Listenable;
import com.pon.ents.base.listenable.ListenerRegistration;
import com.pon.ents.base.queue.CloseableQueue;

public class DynamicPoolExecutor implements CloseableExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicPoolExecutor.class);

    private final CloseableQueue<Runnable> queue;
    private final ListenerRegistration threadCountListenerRegistration;
    private final ThreadFactory threadFactory;
    private final AtomicInteger pendingThreadTerminationCount;

    public DynamicPoolExecutor(
            Opener<CloseableQueue<Runnable>> queueOpener,
            ThreadFactory threadFactory,
            Listenable<Integer> threadCount) {
        this.threadFactory = threadFactory;
        this.queue = queueOpener.open();
        this.pendingThreadTerminationCount = new AtomicInteger(0);
        this.threadCountListenerRegistration = threadCount.register(
                (previous, current) -> adjustThreadCount(current - firstNonNull(previous, 0)));
    }

    @Override
    public void execute(Runnable command) {
        boolean put = queue.put(command);
        Preconditions.checkState(put, "closed");
    }

    @Override
    public void close() {
        threadCountListenerRegistration.close();
        try {
            queue.closeGracefully(-1L);
        } catch (InterruptedException e) {
            throw new WaitInterruptedException(e);
        }
    }

    private void adjustThreadCount(int delta) {
        if (delta > 0) {
            for (int i = 0; i < delta; ++i) {
                Thread thread = threadFactory.newThread(new Worker());
                thread.start();
            }
        } else if (delta < 0) {
            pendingThreadTerminationCount.addAndGet(-delta);
        }
    }

    @Nullable
    private Runnable get() {
        int previousCount = pendingThreadTerminationCount.getAndUpdate(count -> count > 0 ? count - 1 : 0);
        return previousCount > 0 ? null : queue.take();
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            LOG.info("started a thread");
            while (true) {
                @Nullable Runnable command = get();
                if (command == null) {
                    break;
                }
                try {
                    command.run();
                } catch (Throwable t) {
                    LOG.error("a command thrown an uncaught exception", t);
                }
            }
            LOG.info("stopped a thread");
        }
    }
}
