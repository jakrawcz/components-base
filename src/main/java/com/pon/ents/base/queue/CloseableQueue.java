package com.pon.ents.base.queue;

import javax.annotation.Nullable;

import com.pon.ents.base.closeable.RuntimeCloseable;

/**
 * A minimum queue interface with an uninterruptible API and a notion of being "closed".
 * <p>
 * Please note that the {@link #get()} contract enforces non-null elements.
 */
public interface CloseableQueue<T> extends RuntimeCloseable {

    /**
     * A result of {@link CloseableQueue#poll polling}.
     */
    interface Polled<T> {

        /**
         * The polled value itself - only valid if {@link #failureReason()} is {@literal null}.
         */
        T value();

        /**
         * A {@link FailureReason}, or {@literal null} if the {@link CloseableQueue#poll polling} was actually
         * successful.
         */
        @Nullable
        FailureReason failureReason();
    }

    /**
     * A reason for which {@link CloseableQueue#offer offering} or {@link CloseableQueue#poll polling}
     * may not succeed.
     */
    enum FailureReason {

        /**
         * A queue was {@link CloseableBlockingQueue#close}d (before, or while the operation was blocking).
         */
        QUEUE_CLOSED,

        /**
         * A specified (non-negative) maximum blocking duration passed before the operation could finish.
         */
        MAX_DURATION_EXCEEDED;
    }

    /**
     * Uninterruptibly blocks to put the given element on the queue and returns true if the operation finished
     * successfully, or false if the queue was {@link #close}d (possibly in the meantime).
     */
    default boolean put(T element) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    @Nullable FailureReason failureReason = offer(element, -1L);
                    return failureReason == null; // cannot be MAX_DURATION_EXCEEDED
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Blocks (optionally up to the specified {@code maxDurationNanos}) to put the given element on the queue and
     * returns null or a {@link FailureReason} (if the queue was full and/or {@link #close}d).
     * <p>
     * A zero {@code maxDurationNanos} denotes no blocking.
     * <p>
     * A negative {@code maxDurationNanos} denotes no time limit for blocking.
     */
    @Nullable
    FailureReason offer(T element, long maxDurationNanos) throws InterruptedException;

    /**
     * Uninterruptibly blocks and returns the next element from the queue, or {@literal null} if the queue is empty and
     * {@link #close}d.
     */
    @Nullable
    default T take() {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    Polled<T> polled = poll(-1L);
                    return polled.failureReason() == null ? polled.value() : null; // cannot be MAX_DURATION_EXCEEDED
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Blocks (optionally up to the specified {@code maxDurationNanos}) and returns the next {@link Polled} element from
     * the queue (or a special {@link Polled} value if the queue was empty and/or {@link #close}d).
     * <p>
     * A zero {@code maxDurationNanos} denotes no blocking.
     * <p>
     * A negative {@code maxDurationNanos} denotes no time limit for blocking.
     */
    Polled<T> poll(long maxDurationNanos) throws InterruptedException;

    /**
     * Marks that no further {@link #put}s will be successful and blocks (optionally up to the specified
     * {@code maxDurationNanos}) until the queue is empty.
     * <p>
     * A zero {@code maxDurationNanos} denotes no blocking.
     * <p>
     * A negative {@code maxDurationNanos} denotes no time limit for blocking.
     */
    void closeGracefully(long maxDurationNanos) throws InterruptedException;

    /**
     * Uninterruptibly marks that no further {@link #put}s will be successful and returns without blocking.
     * <p>
     * Any elements remaining on the queue may be still taken after this call returns.
     */
    @Override
    default void close() {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    closeGracefully(0L);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
