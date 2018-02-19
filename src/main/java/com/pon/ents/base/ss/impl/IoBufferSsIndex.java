package com.pon.ents.base.ss.impl;

import java.util.Iterator;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;

public class IoBufferSsIndex implements SsIndex {

    private final IoBuffer first;
    private final Iterable<SsIndexEntry> entries;
    private final IoBuffer last;

    public IoBufferSsIndex(IoBuffer first, Iterable<SsIndexEntry> entries, IoBuffer last) {
        this.first = first;
        this.entries = entries;
        this.last = last;
    }

    @Override
    public Input readFirst() {
        return Inputs.fromIoBuffer(first);
    }

    @Override
    public Iterator<SsIndexEntry> intermediateEntryIterator() {
        return entries.iterator();
    }

    @Override
    public Input readLast() {
        return Inputs.fromIoBuffer(last);
    }
}
