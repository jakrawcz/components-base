package com.pon.ents.base.queue.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.queue.CloseableQueue;

public class ArrayCloseableQueue<T> implements CloseableQueue<T> {

    private final Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    private final Condition empty;

    private final Object[] items;
    private int takeIndex;
    private int putIndex;
    private int count;

    private boolean closed;

    public ArrayCloseableQueue(int capacity) {
        Preconditions.checkArgument(capacity > 0);
        this.items = new Object[capacity];
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
        this.empty = lock.newCondition();
        this.putIndex = 0;
        this.takeIndex = 0;
        this.count = 0;
        this.closed = false;
    }

    @Override
    @Nullable
    public FailureReason offer(T e, long maxDurationNanos) throws InterruptedException {
        Preconditions.checkNotNull(e);
        lock.lockInterruptibly();
        try {
            if (closed) {
                return FailureReason.QUEUE_CLOSED;
            }
            if (isFull()) {
                if (maxDurationNanos == 0) {
                    return FailureReason.MAX_DURATION_EXCEEDED;
                }
                if (maxDurationNanos < 0) {
                    while (true) {
                        notFull.await();
                        if (closed) {
                            return FailureReason.QUEUE_CLOSED;
                        }
                        if (!isFull()) {
                            break;
                        }
                    }
                } else {
                    while (true) {
                        maxDurationNanos = notFull.awaitNanos(maxDurationNanos);
                        if (closed) {
                            return FailureReason.QUEUE_CLOSED;
                        }
                        if (!isFull()) {
                            break;
                        }
                        if (maxDurationNanos <= 0) {
                            return FailureReason.MAX_DURATION_EXCEEDED;
                        }
                    }
                }
            }
            enqueue(e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean put(T e) {
        Preconditions.checkNotNull(e);
        lock.lock();
        try {
            if (closed) {
                return false;
            }
            if (isFull()) {
                while (true) {
                    notFull.awaitUninterruptibly();
                    if (closed) {
                        return false;
                    }
                    if (!isFull()) {
                        break;
                    }
                }
            }
            enqueue(e);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Polled<T> poll(long maxDurationNanos) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (closed) {
                return FastPolled.queueClosed();
            }
            if (isEmpty()) {
                if (maxDurationNanos == 0) {
                    return FastPolled.maxDurationExceeded();
                }
                if (maxDurationNanos < 0) {
                    while (true) {
                        notEmpty.await();
                        if (!isEmpty()) {
                            break;
                        }
                        if (closed) {
                            return FastPolled.queueClosed();
                        }
                    }
                } else {
                    while (true) {
                        maxDurationNanos = notEmpty.awaitNanos(maxDurationNanos);
                        if (!isEmpty()) {
                            break;
                        }
                        if (closed) {
                            return FastPolled.queueClosed();
                        }
                        if (maxDurationNanos <= 0) {
                            return FastPolled.maxDurationExceeded();
                        }
                    }
                }
            }
            return FastPolled.successful(dequeue());
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Nullable
    public T take() {
        lock.lock();
        try {
            if (isEmpty()) {
                while (true) {
                    if (closed) {
                        return null;
                    }
                    notEmpty.awaitUninterruptibly();
                    if (!isEmpty()) {
                        break;
                    }
                }
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void closeGracefully(long maxDurationNanos) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            Preconditions.checkState(!closed, "already closed");
            this.closed = true;
            notFull.signalAll();
            notEmpty.signalAll();
            if (maxDurationNanos < 0) {
                while (!isEmpty()) {
                    empty.await();
                }
            } else {
                while (maxDurationNanos > 0 && !isEmpty()) {
                    maxDurationNanos = empty.awaitNanos(maxDurationNanos);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            Preconditions.checkState(!closed, "already closed");
            this.closed = true;
            notFull.signalAll();
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean isFull() {
        return count == items.length;
    }

    private boolean isEmpty() {
        return count == 0;
    }

    private void enqueue(T element) {
        items[putIndex] = element;
        this.putIndex = (putIndex + 1) % items.length;
        ++this.count;
        notEmpty.signal();
    }

    @SuppressWarnings("unchecked")
    private T dequeue() {
        Object element = items[takeIndex];
        items[takeIndex] = null;
        this.takeIndex = (takeIndex + 1) % items.length;
        int newCount = --this.count;
        if (newCount == 0) {
            empty.signal();
        }
        notFull.signal();
        return (T) element;
    }
}
