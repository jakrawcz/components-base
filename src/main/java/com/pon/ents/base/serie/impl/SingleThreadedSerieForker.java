package com.pon.ents.base.serie.impl;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.pon.ents.base.serie.Serie;
import com.pon.ents.base.serie.SerieForker;

public class SingleThreadedSerieForker<T> implements SerieForker<T> {

    private final Serie<T> serie;

    private final AccessQueue<Counted<T>> buffer;

    private int suppliedCount;

    public SingleThreadedSerieForker(Serie<T> serie) {
        this.serie = serie;
        this.buffer = new AccessQueue<>();
        this.suppliedCount = -1;
    }

    @Override
    public Serie<T> get() {
        checkNotClosed();
        --this.suppliedCount;
        return new ForkedSerie();
    }

    @Override
    public void close() {
        checkNotClosed();
        this.suppliedCount = -suppliedCount - 1;
        clearSupplied();
    }

    private void checkNotClosed() {
        Preconditions.checkState(suppliedCount <= 0, "already closed");
    }

    @Nullable
    private T get(long index) {
        Verify.verify(suppliedCount != 0);
        @Nullable Counted<T> countedElement = buffer.access(index);
        if (countedElement == null) {
            @Nullable T element = serie.next();
            if (element == null) {
                return null;
            }
            if (suppliedCount != 1) {
                buffer.add(new Counted<>(element));
            }
            return element;
        }
        countedElement.increment();
        if (countedElement.counts(suppliedCount)) {
            buffer.remove();
        }
        return countedElement.value();
    }

    private void disregard() {
        --this.suppliedCount;
        if (suppliedCount == 0) {
            serie.close();
            return;
        }
        clearSupplied();
    }

    private void clearSupplied() {
        while (true) {
            @Nullable Counted<T> countedElement = buffer.peek();
            if (countedElement == null) {
                break;
            }
            if (countedElement.counts(suppliedCount)) {
                buffer.remove();
            } else {
                break;
            }
        }
    }

    private long remainingWith(long removedCount) {
        long serieRemaining = serie.remaining();
        if (serieRemaining == -1) {
            return -1;
        }
        long bufferedCount = buffer.addedCount() - removedCount;
        return serieRemaining + bufferedCount;
    }

    private class ForkedSerie implements Serie<T> {

        private static final int CLOSED_MARKER = -2;
        private static final int END_MARKER = -1;

        private long nextCalledCount;

        public ForkedSerie() {
            this.nextCalledCount = 0;
        }

        @Override
        @Nullable
        public T next() {
            checkNotClosed();
            if (nextCalledCount == END_MARKER) {
                return null;
            }
            @Nullable T element = get(nextCalledCount);
            if (element == null) {
                this.nextCalledCount = END_MARKER;
            } else {
                ++this.nextCalledCount;
            }
            return element;
        }

        @Override
        public long remaining() {
            checkNotClosed();
            return remainingWith(nextCalledCount);
        }

        @Override
        public void close() {
            checkNotClosed();
            this.nextCalledCount = CLOSED_MARKER;
            disregard();
        }

        private void checkNotClosed() {
            Preconditions.checkArgument(nextCalledCount != CLOSED_MARKER, "already closed");
        }
    }

    private static class Counted<T> {

        private final T value;

        private int count;

        public Counted(T value) {
            this.value = value;
            this.count = 1;
        }

        public T value() {
            return value;
        }

        public void increment() {
            ++this.count;
        }

        public boolean counts(int expectedCount) {
            return count == expectedCount;
        }

        @Override
        public String toString() {
            return "(" + count + ") " + value;
        }
    }
}
