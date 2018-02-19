package com.pon.ents.base.ss.impl;

import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.ss.SortedInputs;
import com.pon.ents.base.ss.SsTable;

public class SubSsTable implements SsTable {

    private final SsTable underlying;
    private final Input from;
    private final Input to;

    public SubSsTable(SsTable underlying, Input from, Input to) {
        this.underlying = underlying;
        this.from = from;
        this.to = to;
    }

    @Override
    public CloseableIterator<Input> iterator(Input furtherFrom, Input earlierTo) {
        return underlying.iterator(SortedInputs.greaterOf(from, furtherFrom), SortedInputs.lesserOf(earlierTo, to));
    }
}
