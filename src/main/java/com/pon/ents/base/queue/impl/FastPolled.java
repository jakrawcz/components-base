package com.pon.ents.base.queue.impl;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.queue.CloseableQueue.FailureReason;
import com.pon.ents.base.queue.CloseableQueue.Polled;

@SuppressWarnings("unchecked")
public class FastPolled<T> implements Polled<T> {

    private static final Polled<?> MAX_DURATION_EXCEEDED = new FastPolled<>(FailureReason.MAX_DURATION_EXCEEDED);
    private static final Polled<?> QUEUE_CLOSED = new FastPolled<>(FailureReason.QUEUE_CLOSED);

    private final Object valueOrFailureReason;

    private FastPolled(Object valueOrMarker) {
        this.valueOrFailureReason = valueOrMarker;
    }

    public static <T> Polled<T> successful(T value) {
        Preconditions.checkArgument(value.getClass() != FailureReason.class);
        return new FastPolled<>(value);
    }

    public static <T> Polled<T> maxDurationExceeded() {
        return (Polled<T>) MAX_DURATION_EXCEEDED;
    }

    public static <T> Polled<T> queueClosed() {
        return (Polled<T>) QUEUE_CLOSED;
    }

    @Override
    public T value() {
        if (valueOrFailureReason.getClass() == FailureReason.class) {
            throw new IllegalStateException("poll was not successful");
        }
        return (T) valueOrFailureReason;
    }

    @Override
    @Nullable
    public FailureReason failureReason() {
        if (valueOrFailureReason.getClass() == FailureReason.class) {
            return (FailureReason) valueOrFailureReason;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        @Nullable FailureReason failureReason = failureReason();
        if (failureReason != null) {
            return "[" + failureReason + "]";
        } else {
            return value().toString();
        }
    }
}
