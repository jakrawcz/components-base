package com.pon.ents.base.ss.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.io.Input;

public class InputSlicingCloseableIterator extends AbstractIterator<Input> implements CloseableIterator<Input> {

    private final Input input;
    private final Slice slice;
    private final SsLengthCodec lengthCodec;

    public InputSlicingCloseableIterator(Input input, SsLengthCodec lengthCodec) {
        this.input = input;
        this.slice = new Slice();
        this.lengthCodec = lengthCodec;
    }

    @Override
    protected Input computeNext() {
        slice.skipRest();
        long length = lengthCodec.decode(input);
        if (length == -1) {
            return endOfData();
        }
        slice.limitTo(length);
        return slice;
    }

    @Override
    public void close() {
        input.close();
    }

    private class Slice implements Input {

        private static final long CLOSED_MANUALLY = -1;
        private static final long CLOSED_BY_REACHING_LIMIT = -2;

        private long remaining;

        public void limitTo(long limit) {
            this.remaining = limit;
        }

        public void skipRest() {
            if (remaining > 0) { // critical because of marker values
                input.skipFully(remaining);
            }
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
                return -1;
            }
            int toRead = (int) Math.min(length, remaining);
            int read = input.read(buffer, offset, toRead);
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
                return -1;
            }
            int read = input.read();
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
            this.remaining = CLOSED_MANUALLY;
        }

        private void checkNotClosedManually() {
            Preconditions.checkState(remaining != CLOSED_MANUALLY, "already closed");
        }
    }
}
