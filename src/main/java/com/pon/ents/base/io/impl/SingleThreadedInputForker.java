package com.pon.ents.base.io.impl;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.primitives.Ints;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpeners.InputForker;
import com.pon.ents.base.io.ex.RuntimeEofException;

public class SingleThreadedInputForker implements InputForker {

    private static final int STILL_OPENING = -1;

    private final Input input;
    private final IoBufferBuilder builder;
    private final SortedSet<ForkedInput> forkedInputs;

    private int truncatedByteCount;

    public SingleThreadedInputForker(Input input) {
        this.input = input;
        this.builder = new IoBufferBuilder();
        this.forkedInputs = new TreeSet<>();
        this.truncatedByteCount = STILL_OPENING;
    }

    @Override
    public Input openAt(long longAt) {
        Preconditions.checkArgument(longAt >= 0 && longAt <= Integer.MAX_VALUE,
                "cannot support buffering of %s bytes", longAt);
        int at = Ints.checkedCast(longAt);
        int readAt = builder.declareRead();
        int bufferMissing = at - readAt;
        if (bufferMissing > 0) {
            int writeAt = builder.declareWrite(bufferMissing);
            Verify.verify(writeAt == readAt);
            try {
                input.readFully(builder.access(), writeAt, bufferMissing);
            } catch (RuntimeEofException e) {
                throw new IllegalArgumentException("offset " + at + " is greater than input's length", e);
            }
        }
        ForkedInput forkedInput = new ForkedInput(at);
        forkedInputs.add(forkedInput);
        return forkedInput;
    }

    @Override
    public void opened() {
        Preconditions.checkState(truncatedByteCount == STILL_OPENING, "already notified");
        this.truncatedByteCount = 0;
        truncateToLowestAt();
    }

    @Override
    public void close() {
        input.close();
    }

    private void onLowestAtIncreased() {
        if (truncatedByteCount == STILL_OPENING) {
            return; // ignore it if we still need to buffer everything
        }
        truncateToLowestAt();
    }

    private void truncateToLowestAt() {
        ForkedInput lowestAtForkedInput = forkedInputs.first();
        int lowestNonTruncatedAt = lowestAtForkedInput.nonTruncatedAt();
        int toTruncate = lowestNonTruncatedAt - truncatedByteCount;
        if (toTruncate > 0) {
            truncate(toTruncate);
        }
    }

    private void truncate(int additionalByteCount) {
        byte[] buffer = builder.access();
        int readAt = builder.declareRead();
        System.arraycopy(buffer, additionalByteCount, buffer, 0, readAt);
        builder.declareErased(additionalByteCount);
        this.truncatedByteCount += additionalByteCount;
    }

    private int toTruncated(int nonTruncatedAt) {
        if (truncatedByteCount == STILL_OPENING) {
            return nonTruncatedAt;
        }
        int at = nonTruncatedAt - truncatedByteCount;
        Verify.verify(at >= 0);
        return at;
    }

    private int readAt(int nonTruncatedAt, byte[] buffer, int offset, int length) {
        if (length == 0) {
            return 0;
        }
        int at = toTruncated(nonTruncatedAt);
        int readAt = builder.declareRead();
        int bufferAvailable = readAt - at;
        if (bufferAvailable > 0) {
            int toRead = Math.min(bufferAvailable, length);
            System.arraycopy(builder.access(), readAt, buffer, offset, toRead);
            return toRead;
        }
        Verify.verify(readAt == at);
        int read = input.read(buffer, offset, length);
        if (read == -1) {
            return -1;
        }
        int writeAt = builder.declareWrite(read);
        Verify.verify(writeAt == readAt);
        System.arraycopy(buffer, offset, builder.access(), writeAt, read);
        return read;
    }

    private int readAt(int nonTruncatedAt) {
        int at = toTruncated(nonTruncatedAt);
        int readAt = builder.declareRead();
        if (at < readAt) {
            return builder.access()[at];
        }
        Verify.verify(readAt == at);
        int read = input.read();
        if (read == -1) {
            return -1;
        }
        int writeAt = builder.declareWrite(1);
        Verify.verify(writeAt == readAt);
        builder.access()[at] = (byte) read;
        return read;
    }

    private long remainingAt(int nonTruncatedAt) {
        int at = toTruncated(nonTruncatedAt);
        long inputRemaining = input.remaining();
        if (inputRemaining == -1) {
            return -1;
        }
        int readAt = builder.declareRead();
        int bufferAvailable = readAt - at;
        return bufferAvailable + inputRemaining;
    }

    private void onAtIncreased(ForkedInput forkedInput) {
        boolean lowestAtWillIncrease = forkedInput == forkedInputs.first();
        forkedInputs.remove(forkedInput);
        forkedInputs.add(forkedInput);
        if (lowestAtWillIncrease) {
            onLowestAtIncreased();
        }
    }

    private void onClosed(ForkedInput closedForkedInput) {
        ForkedInput lowestAtForkedInput = forkedInputs.first();
        if (lowestAtForkedInput == closedForkedInput) {
            forkedInputs.remove(closedForkedInput);
            onLowestAtIncreased();
        } else {
            boolean removed = forkedInputs.remove(closedForkedInput);
            Verify.verify(removed);
        }
    }

    private class ForkedInput implements Input, Comparable<ForkedInput> {

        private static final int CLOSED = -1;

        private int nonTruncatedAt;

        public ForkedInput(int nonTruncatedAt) {
            this.nonTruncatedAt = nonTruncatedAt;
        }

        private int nonTruncatedAt() {
            return nonTruncatedAt;
        }

        @Override
        public void close() {
            checkNotClosed();
            onClosed(this);
            this.nonTruncatedAt = CLOSED;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) {
            int read = readAt(nonTruncatedAt, buffer, offset, length);
            if (read != -1) {
                this.nonTruncatedAt += read;
            }
            onAtIncreased(this);
            return read;
        }

        @Override
        public int read() {
            int read = readAt(nonTruncatedAt);
            if (read != -1) {
                ++this.nonTruncatedAt;
            }
            onAtIncreased(this);
            return read;
        }

        @Override
        public long remaining() {
            return remainingAt(nonTruncatedAt);
        }

        @Override
        public int compareTo(ForkedInput other) {
            int atComparison = Integer.compare(nonTruncatedAt, other.nonTruncatedAt());
            if (atComparison != 0) {
                return atComparison;
            }
            return Integer.compare(hashCode(), other.hashCode()); // sort by identity
        }

        private void checkNotClosed() {
            Preconditions.checkState(nonTruncatedAt != CLOSED, "already closed");
        }
    }
}
