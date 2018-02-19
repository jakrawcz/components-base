package com.pon.ents.base.io.impl;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.Input;

public class LimitedInput implements Input {

    private static final long CLOSED_MANUALLY = -1;
    private static final long CLOSED_BY_REACHING_LIMIT = -2;

    private final Input underlying;
    private long remaining;

    public LimitedInput(Input underlying, long limit) {
        Preconditions.checkArgument(limit >= 0, "cannot limit to %s", limit);
        this.underlying = underlying;
        this.remaining = limit;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        checkNotClosedManually();
        if (length == 0) {
            return 0;
        }
        if (remaining == CLOSED_BY_REACHING_LIMIT) {
            return -1;
        }
        if (remaining == 0) {
            this.remaining = CLOSED_BY_REACHING_LIMIT;
            underlying.close();
            return -1;
        }
        int toRead = (int) Math.min(length, remaining);
        int read = underlying.read(buffer, offset, toRead);
        if (read != -1) {
            this.remaining -= read;
        }
        return read;
    }

    @Override
    public int read() {
        checkNotClosedManually();
        if (remaining == CLOSED_BY_REACHING_LIMIT) {
            return -1;
        }
        if (remaining == 0) {
            this.remaining = CLOSED_BY_REACHING_LIMIT;
            underlying.close();
            return -1;
        }
        int read = underlying.read();
        if (read != -1) {
            --this.remaining;
        }
        return read;
    }

    @Override
    public long remaining() {
        checkNotClosedManually();
        return remaining;
    }

    @Override
    public void close() {
        checkNotClosedManually();
        long previousRemaining = remaining;
        this.remaining = CLOSED_MANUALLY;
        if (previousRemaining != CLOSED_BY_REACHING_LIMIT) {
            underlying.close();
        }
    }

    private void checkNotClosedManually() {
        Preconditions.checkState(remaining != CLOSED_MANUALLY, "already closed");
    }
}
