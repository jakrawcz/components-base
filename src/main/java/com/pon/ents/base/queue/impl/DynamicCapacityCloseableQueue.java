package com.pon.ents.base.queue.impl;

import static com.pon.ents.base.equivalence.Equivalences.identityOf;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.annotation.Nullable;

import com.google.common.base.Equivalence.Wrapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.pon.ents.base.listenable.Listenable;
import com.pon.ents.base.listenable.ListenerRegistration;
import com.pon.ents.base.queue.CloseableQueue;

public class DynamicCapacityCloseableQueue<T> implements CloseableQueue<T> {

    public interface CapacityQueueFactory<T> {

        CloseableQueue<T> create(int capacity);
    }

    private final CapacityQueueFactory<T> factory;
    private final Deque<Wrapper<CloseableQueue<T>>> underlyingQueues;
    private final ListenerRegistration capacityListenerRegistration;

    public DynamicCapacityCloseableQueue(CapacityQueueFactory<T> factory, Listenable<Integer> capacity) {
        this.factory = factory;
        this.underlyingQueues = new ConcurrentLinkedDeque<>();
        this.capacityListenerRegistration =
                capacity.register((previous, currentCapacity) -> setCapacity(previous == null, currentCapacity));
        Verify.verify(underlyingQueues.size() == 1, "same-thread first call listener registration policy violated");
    }

    @Override
    @Nullable
    public FailureReason offer(T element, long maxDurationNanos) throws InterruptedException {
        if (maxDurationNanos <= 0) {
            while (true) {
                @Nullable CloseableQueue<T> lastQueue = peekLast();
                if (lastQueue == null) {
                    return FailureReason.QUEUE_CLOSED;
                }
                @Nullable FailureReason failureReason = lastQueue.offer(element, maxDurationNanos);
                if (failureReason != FailureReason.QUEUE_CLOSED) {
                    return failureReason;
                }
            }
        } else {
            long currentNanoTime = System.nanoTime();
            long endNanoTime = currentNanoTime + maxDurationNanos;
            while (true) {
                @Nullable CloseableQueue<T> lastQueue = peekLast();
                if (lastQueue == null) {
                    return FailureReason.QUEUE_CLOSED;
                }
                @Nullable FailureReason failureReason = lastQueue.offer(element, endNanoTime - currentNanoTime);
                if (failureReason != FailureReason.QUEUE_CLOSED) {
                    return failureReason;
                }
                currentNanoTime = System.nanoTime();
                if (currentNanoTime >= endNanoTime) {
                    return FailureReason.MAX_DURATION_EXCEEDED;
                }
            }
        }
    }

    @Override
    public boolean put(T e) {
        while (true) {
            @Nullable CloseableQueue<T> lastQueue = peekLast();
            if (lastQueue == null) {
                return false;
            }
            boolean put = lastQueue.put(e);
            if (put) {
                return true;
            }
        }
    }

    @Override
    public Polled<T> poll(long maxDurationNanos) throws InterruptedException {
        if (maxDurationNanos <= 0) {
            while (true) {
                @Nullable CloseableQueue<T> firstQueue = peekFirst();
                if (firstQueue == null) {
                    return FastPolled.queueClosed();
                }
                Polled<T> polled = firstQueue.poll(maxDurationNanos);
                if (polled.failureReason() != FailureReason.QUEUE_CLOSED) {
                    return polled;
                }
                underlyingQueues.remove(identityOf(firstQueue));
            }
        } else {
            long currentNanoTime = System.nanoTime();
            long endNanoTime = currentNanoTime + maxDurationNanos;
            while (true) {
                @Nullable CloseableQueue<T> firstQueue = peekFirst();
                if (firstQueue == null) {
                    return FastPolled.queueClosed();
                }
                Polled<T> polled = firstQueue.poll(endNanoTime - currentNanoTime);
                if (polled.failureReason() != FailureReason.QUEUE_CLOSED) {
                    return polled;
                }
                underlyingQueues.remove(identityOf(firstQueue));
                currentNanoTime = System.nanoTime();
                if (currentNanoTime >= endNanoTime) {
                    return FastPolled.maxDurationExceeded();
                }
            }
        }
    }

    @Override
    @Nullable
    public T take() {
        while (true) {
            @Nullable CloseableQueue<T> firstQueue = peekFirst();
            if (firstQueue == null) {
                return null;
            }
            @Nullable T element = firstQueue.take();
            if (element != null) {
                return element;
            }
            underlyingQueues.remove(identityOf(firstQueue));
        }
    }

    @Override
    public void closeGracefully(long maxDurationNanos) throws InterruptedException {
        synchronized (underlyingQueues) {
            capacityListenerRegistration.close();
            @Nullable CloseableQueue<T> previousLastQueue = peekLast();
            Preconditions.checkArgument(previousLastQueue != null, "already closed");
            previousLastQueue.closeGracefully(maxDurationNanos);
        }
    }

    private void setCapacity(boolean initial, int capacity) {
        synchronized (underlyingQueues) {
            @Nullable CloseableQueue<T> previousLastQueue = peekLast();
            underlyingQueues.addLast(identityOf(factory.create(capacity)));
            if (!initial) {
                Preconditions.checkArgument(previousLastQueue != null, "already closed");
                previousLastQueue.close();
            }
        }
    }

    @Nullable
    private CloseableQueue<T> peekLast() {
        return unwrap(underlyingQueues.peekLast());
    }

    @Nullable
    private CloseableQueue<T> peekFirst() {
        return unwrap(underlyingQueues.peekFirst());
    }

    @Nullable
    private CloseableQueue<T> unwrap(@Nullable Wrapper<CloseableQueue<T>> wrapper) {
        return wrapper == null ? null : wrapper.get();
    }
}
