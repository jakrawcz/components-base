package com.pon.ents.base.ss.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpeners;
import com.pon.ents.base.io.InputOpeners.InputForker;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.serie.Serie;
import com.pon.ents.base.serie.Series;
import com.pon.ents.base.ss.SortedIoBuffers;
import com.pon.ents.base.ss.SsTable;

public class MergingSsTable implements SsTable {

    private final Iterable<SsTable> ssTables;

    public MergingSsTable(Iterable<SsTable> ssTables) {
        this.ssTables = ssTables;
    }

    @Override
    public Serie<Input> get(Input from, Input to) {
        Collection<? extends Serie<? extends IoBuffer>> series = openSsTables(from, to);
        return Series.mergingSorted(SortedIoBuffers.comparator(), series)
                .transform(Inputs::fromIoBuffer);
    }

    private Collection<Serie<IoBuffer>> openSsTables(Input from, Input to) {
        try (InputForker fromForker = InputOpeners.forkingForOneThread(from);
                InputForker toForker = InputOpeners.forkingForOneThread(to)) {
            return Streams.stream(ssTables.iterator())
                    .map(ssTable -> ssTable.get(fromForker.open(), toForker.open()).transform(Inputs::toIoBuffer))
                    .collect(Collectors.toList());
        }
    }
}
