package com.pon.ents.base.io.impl;

import com.google.common.primitives.Ints;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpener;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;

public class IoBufferInputOpener implements InputOpener {

    private final IoBuffer ioBuffer;

    public IoBufferInputOpener(IoBuffer ioBuffer) {
        this.ioBuffer = ioBuffer;
    }

    @Override
    public Input openAt(long offset) {
        return Inputs.fromByteArray(ioBuffer.buffer(), ioBuffer.offset() + Ints.checkedCast(offset), ioBuffer.limit());
    }
}
