package com.pon.ents.base.ss.impl;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterators;
import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.closeable.CloseableIterators;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.ss.SortedIoBuffers;
import com.pon.ents.base.ss.SsTable;

public class ListSsTable implements SsTable {

    private final List<IoBuffer> ioBuffers;

    public ListSsTable(List<IoBuffer> ioBuffers) {
        this.ioBuffers = ioBuffers;
    }

    @Override
    public CloseableIterator<Input> iterator(Input fromInput, Input toInput) {
        IoBuffer from = Inputs.toIoBuffer(fromInput);
        int fromSearchResult = Collections.binarySearch(ioBuffers, from, SortedIoBuffers.comparator());
        int fromIndex = fromSearchResult < 0 ? -fromSearchResult - 1 : fromSearchResult;

        if (fromIndex == ioBuffers.size()) {
            return CloseableIterators.empty();
        }

        IoBuffer to = Inputs.toIoBuffer(toInput);
        int toSearchResult = Collections.binarySearch(ioBuffers, to, SortedIoBuffers.comparator());
        int toIndex = toSearchResult < 0 ? -toSearchResult - 1 : toSearchResult + 1;

        List<IoBuffer> selectedIoBuffers = ioBuffers.subList(fromIndex, toIndex);
        return CloseableIterators.adapt(Iterators.transform(selectedIoBuffers.iterator(), Inputs::fromIoBuffer));
    }
}
