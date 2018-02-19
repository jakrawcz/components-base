package com.pon.ents.base.ss.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;
import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.closeable.CloseableIterators;
import com.pon.ents.base.closeable.RuntimeCloseables;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpeners;
import com.pon.ents.base.io.InputOpeners.InputForker;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.serie.SortedIterators;
import com.pon.ents.base.ss.SortedIoBuffers;
import com.pon.ents.base.ss.SsTable;

public class MergingSsTable implements SsTable {

    private final Iterable<SsTable> ssTables;

    public MergingSsTable(Iterable<SsTable> ssTables) {
        this.ssTables = ssTables;
    }

    @Override
    public CloseableIterator<Input> iterator(Input from, Input to) {
        Collection<CloseableIterator<IoBuffer>> closeableIterators = openSsTables(from, to);
        Iterator<IoBuffer> sortedIoBufferIterator =
                SortedIterators.mergingSorted(SortedIoBuffers.comparator(), closeableIterators);
        Iterator<Input> sortedIterator = Iterators.transform(sortedIoBufferIterator, Inputs::fromIoBuffer);
        return CloseableIterators.adapt(sortedIterator, RuntimeCloseables.composite(closeableIterators));
    }

    private Collection<CloseableIterator<IoBuffer>> openSsTables(Input from, Input to) {
        try (InputForker fromForker = InputOpeners.forkingForOneThread(from);
                InputForker toForker = InputOpeners.forkingForOneThread(to)) {
            return Streams.stream(ssTables.iterator())
                    .map(ssTable -> ssTable.iterator(fromForker.open(), toForker.open()))
                    .map(closeableIterator -> CloseableIterators.transform(closeableIterator, Inputs::toIoBuffer))
                    .collect(Collectors.toList());
        }
    }
}
