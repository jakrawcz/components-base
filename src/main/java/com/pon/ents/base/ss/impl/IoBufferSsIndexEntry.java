package com.pon.ents.base.ss.impl;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.ss.impl.SsIndex.SsIndexEntry;

public class IoBufferSsIndexEntry implements SsIndexEntry {

    private final IoBuffer prefixIoBuffer;
    private final long offset;

    public IoBufferSsIndexEntry(IoBuffer prefixIoBuffer, long offset) {
        this.prefixIoBuffer = prefixIoBuffer;
        this.offset = offset;
    }

    @Override
    public Input readPrefix() {
        return Inputs.fromIoBuffer(prefixIoBuffer);
    }

    @Override
    public long offset() {
        return offset;
    }

    @Override
    public int hashCode() {
        return prefixIoBuffer.hashCode() + 31 * Long.hashCode(offset);
    }

    @Override
    public boolean equals(Object object) {
        if (object.getClass() != IoBufferSsIndexEntry.class) {
            return false;
        }
        IoBufferSsIndexEntry other = (IoBufferSsIndexEntry) object;
        return prefixIoBuffer.equals(other.prefixIoBuffer) && offset == other.offset;
    }

    @Override
    public String toString() {
        return offset + ": " + prefixIoBuffer;
    }
}