package com.pon.ents.base.blob.impl;

import com.google.common.primitives.Ints;
import com.pon.ents.base.blob.Blob;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.IoBuffers;
import com.pon.ents.base.io.Output;
import com.pon.ents.base.io.Outputs;

public class IoBufferBlob implements Blob {

    private IoBuffer ioBuffer;

    public IoBufferBlob() {
        this.ioBuffer = IoBuffers.empty();
    }

    @Override
    public Input read(long relativeOffset) {
        // the internal IoBuffer is kept zero-offset
        return Inputs.fromByteArray(ioBuffer.buffer(), toOffset(relativeOffset));
    }

    @Override
    public Output write(long relativeOffset) {
        return Outputs.toIoBuffer(this::replace);
    }

    @Override
    public long length() {
        return ioBuffer.limit();
    }

    @Override
    public void truncateTo(long relativeOffset) {
        replace(ioBuffer.rePositioned(0, toOffset(relativeOffset)));
    }

    @Override
    public void clear() {
        replace(IoBuffers.empty());
    }

    private int toOffset(long relativeOffset) {
        int intRelativeOffset = Ints.checkedCast(relativeOffset);
        return (int) RelativeOffsets.toOffset(intRelativeOffset, length());
    }

    private void replace(IoBuffer ioBuffer) {
        this.ioBuffer = ioBuffer;
    }
}
