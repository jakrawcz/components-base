package com.pon.ents.base.io.impl;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.ex.RuntimeIoException;

public class InputFromInputStream implements Input {

    private static final long CLOSED = Long.MIN_VALUE;

    private final InputStream underlying;

    private long remaining;

    public InputFromInputStream(InputStream underlying, long size) {
        this.underlying = underlying;
        this.remaining = size;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        checkNotClosed();
        try {
            int read = underlying.read(buffer, offset, length);
            if (remaining == -1) { // TODO: CONSIDER: the known/unknown distinction is strong; specialize into classes?
                if (read == -1) {
                    this.remaining = 0;
                }
            } else {
                if (read == -1) {
                    Preconditions.checkState(remaining == 0, "the underlying stream is at its end, "
                            + "but the declared size suggests there were %s bytes remaining", remaining);
                } else {
                    Preconditions.checkState(remaining >= read, "the underlying stream just provided %s bytes, "
                            + "but the declared size suggests there were %s bytes remaining", read, remaining);
                    this.remaining -= read;
                }
            }
            return read;
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    @Override
    public long remaining() {
        checkNotClosed();
        return remaining;
    }

    @Override
    public void close() {
        checkNotClosed();
        this.remaining = CLOSED;
    }

    @Override
    public int read() {
        checkNotClosed();
        try {
            int read = underlying.read();
            if (remaining != -1) {
                if (read == -1) {
                    Preconditions.checkState(remaining == 0, "the underlying stream is at its end, "
                            + "but the declared size suggests there were %s bytes remaining", remaining);
                } else {
                    Preconditions.checkState(remaining > 0, "the underlying stream just provided a byte, "
                            + "but the declared size suggests it was at its end");
                    --this.remaining;
                }
            }
            return read;
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    private void checkNotClosed() {
        Preconditions.checkState(remaining != CLOSED, "already closed");
    }
}
