package com.pon.ents.base.queue.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.pon.ents.base.queue.CloseableQueue;


public class SynchronousCloseableQueue<T> implements CloseableQueue<T> {

    private static final long LOCK_TIMEOUT = Long.MIN_VALUE;

    private final Lock giverLock;
    private final Lock takerLock;

    @Nullable
    private Exchanger<T> exchanger;

    private final Object exchangerLock;
    private final AtomicBoolean closed;

    public SynchronousCloseableQueue() {
        this.giverLock = new ReentrantLock();
        this.takerLock = new ReentrantLock();
        this.exchangerLock = new Object();
        this.exchanger = null;
        this.closed = new AtomicBoolean();
    }

    @Override
    @Nullable
    public FailureReason offer(T element, long maxDurationNanos) throws InterruptedException {
        Preconditions.checkNotNull(element);

        if (closed.get()) {
            return FailureReason.QUEUE_CLOSED;
        }
        maxDurationNanos = lock(giverLock, maxDurationNanos);
        if (closed.get()) {
            return FailureReason.QUEUE_CLOSED;
        }
        if (maxDurationNanos == LOCK_TIMEOUT) {
            return FailureReason.MAX_DURATION_EXCEEDED;
        }
        try {
            Giver<T> giver;
            synchronized (exchangerLock) {
                if (closed.get()) {
                    return FailureReason.QUEUE_CLOSED;
                }
                if (exchanger == null) {
                    giver = new Giver<>(element);
                    this.exchanger = giver;
                } else {
                    Taker<T> taker = (Taker<T>) exchanger;
                    taker.take(element);
                    this.exchanger = null;
                    return null;
                }
            }
            giver.await(maxDurationNanos);
            synchronized (exchangerLock) {
                if (exchanger == null) {
                    if (closed.get()) {
                        return FailureReason.QUEUE_CLOSED;
                    }
                    return null;
                } else {
                    this.exchanger = null;
                    return FailureReason.MAX_DURATION_EXCEEDED;
                }
            }
        } finally {
            giverLock.unlock();
        }
    }

    @Override
    public Polled<T> poll(long maxDurationNanos) throws InterruptedException {
        maxDurationNanos = lock(takerLock, maxDurationNanos);
        if (maxDurationNanos == LOCK_TIMEOUT) {
            return FastPolled.maxDurationExceeded();
        }
        try {
            Taker<T> taker;
            synchronized (exchangerLock) {
                if (exchanger == null) {
                    if (closed.get()) {
                        return FastPolled.queueClosed();
                    }
                    taker = new Taker<>();
                    this.exchanger = taker;
                } else {
                    Giver<T> giver = (Giver<T>) exchanger;
                    T element = giver.give();
                    this.exchanger = null;
                    return FastPolled.successful(element);
                }
            }
            taker.await(maxDurationNanos);
            synchronized (exchangerLock) {
                if (exchanger == null) {
                    if (closed.get()) {
                        return FastPolled.queueClosed();
                    }
                    return FastPolled.successful(taker.taken());
                } else {
                    this.exchanger = null;
                    return FastPolled.maxDurationExceeded();
                }
            }
        } finally {
            takerLock.unlock();
        }
    }

    @Override
    public void closeGracefully(long maxDurationNanos) throws InterruptedException {
        // flip the flag for any of the giver/taker checks above
        boolean set = closed.compareAndSet(false, true);
        Preconditions.checkState(set, "already closed");
        synchronized (exchangerLock) {
            // signal the thread waiting on the exchanger (if any); it will check the closed flag right away
            if (exchanger != null) {
                exchanger.awaited();
                this.exchanger = null;
            }
        }
        maxDurationNanos = lock(takerLock, maxDurationNanos);
        if (maxDurationNanos == LOCK_TIMEOUT) {
            return;
        }
        try {
            maxDurationNanos = lock(giverLock, maxDurationNanos);
            if (maxDurationNanos == LOCK_TIMEOUT) {
                return;
            }
            try {
                // just acquire the locks to make sure that all active givers / takers finished
            } finally {
                giverLock.unlock();
            }
        } finally {
            takerLock.unlock();
        }
    }

    private static long lock(Lock lock, long maxDurationNanos) throws InterruptedException {
        if (maxDurationNanos == 0) {
            boolean locked = lock.tryLock();
            return locked ? 0 : LOCK_TIMEOUT;
        }
        if (maxDurationNanos < 0) {
            lock.lockInterruptibly();
            return -1L;
        } else {
            long startNanoTime = System.nanoTime();
            boolean locked = lock.tryLock(maxDurationNanos, TimeUnit.NANOSECONDS);
            if (locked) {
                long tookNanos = System.nanoTime() - startNanoTime;
                return Math.max(0L, maxDurationNanos - tookNanos);
            } else {
                return LOCK_TIMEOUT;
            }
        }
    }

    private abstract static class Exchanger<T> {

        private final CountDownLatch latch;

        public Exchanger() {
            this.latch = new CountDownLatch(1);
        }

        public void await(long maxDurationNanos) throws InterruptedException {
            if (maxDurationNanos < 0) {
                latch.await();
            } else {
                // the 0 is handled naturally as well
                latch.await(maxDurationNanos, TimeUnit.NANOSECONDS);
            }
        }

        public void awaited() {
            latch.countDown();
        }
    }

    private static class Giver<T> extends Exchanger<T> {

        private final T element;

        public Giver(T element) {
            this.element = element;
        }

        public T give() {
            awaited();
            return element;
        }
    }

    private static class Taker<T> extends Exchanger<T> {

        @Nullable
        private T element;

        public void take(T element) {
            this.element = element;
            awaited();
        }

        public T taken() {
            Verify.verify(element != null);
            return element;
        }
    }
}
