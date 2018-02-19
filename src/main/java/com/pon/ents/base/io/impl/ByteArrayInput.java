package com.pon.ents.base.io.impl;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.Input;

public class ByteArrayInput implements Input {

    @Nullable
    private byte[] byteArray;

    private final int limit;

    private int at;

    public ByteArrayInput(byte[] byteArray, int offset, int limit) {
        Preconditions.checkArgument(offset >= 0 && offset <= byteArray.length);
        Preconditions.checkArgument(limit >= offset && limit <= byteArray.length);
        this.byteArray = Preconditions.checkNotNull(byteArray);
        this.at = offset;
        this.limit = limit;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        checkNotClosed();
        if (length == 0) {
            return 0;
        }
        int readable = limit - at;
        if (readable == 0) {
            return -1;
        }
        int toRead = Math.min(readable, length);
        System.arraycopy(byteArray, at, buffer, offset, toRead);
        this.at += toRead;
        return toRead;
    }
    @Override
    public long remaining() {
        checkNotClosed();
        return limit - at;
    }

    @Override
    public void close() {
        checkNotClosed();
        this.byteArray = null;
    }

    @Override
    public int read() {
        checkNotClosed();
        if (at == limit) {
            return -1;
        }
        return byteArray[this.at++];
    }

    @Override
    public long skip(long count) {
        checkNotClosed();
        int skippable = limit - at;
        int toSkip = (int) Math.min(skippable, count);
        this.at += toSkip;
        return toSkip;
    }

    private void checkNotClosed() {
        Preconditions.checkState(byteArray != null, "already closed");
    }
}
