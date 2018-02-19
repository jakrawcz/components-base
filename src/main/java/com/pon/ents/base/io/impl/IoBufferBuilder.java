package com.pon.ents.base.io.impl;

import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.IoBuffers;

public class IoBufferBuilder {

    private static final int GROW_FACTOR = 2;

    private byte[] byteArray;
    private int length;

    public IoBufferBuilder() {
        this(IoBuffers.empty());
    }

    public IoBufferBuilder(IoBuffer ioBuffer) {
        Preconditions.checkArgument(ioBuffer.offset() == 0, "non-zero offset: %s", ioBuffer.offset());
        this.byteArray = ioBuffer.buffer();
        this.length = ioBuffer.limit();
    }

    public int declareRead() {
        return length;
    }

    public int declareWrite(int additionalLength) {
        int capacity = byteArray.length;
        int remainingCapacity = capacity - length;
        int newLength = length + additionalLength;
        if (remainingCapacity < additionalLength) {
            int newCapacity = Math.max(GROW_FACTOR * capacity, newLength);
            try {
                this.byteArray = Arrays.copyOf(byteArray, newCapacity);
            } catch (OutOfMemoryError e) {
                throw new IllegalStateException("could not allocate an array of " + newCapacity + " bytes (after a "
                        + "buffer of " + capacity + " bytes capacity needed to accomodate additional "
                        + additionalLength + " bytes)", e);
            }
        }
        int at = length;
        this.length = newLength;
        return at;
    }

    public void declareErased(int erasedLength) {
        this.length -= erasedLength;
        if (length < byteArray.length / GROW_FACTOR) {
            this.byteArray = Arrays.copyOf(byteArray, length);
        }
    }

    public byte[] access() {
        return byteArray;
    }

    public IoBuffer snapshot() {
        return IoBuffers.wrap(byteArray, 0, length);
    }
}
