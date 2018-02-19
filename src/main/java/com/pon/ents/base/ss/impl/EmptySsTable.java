package com.pon.ents.base.ss.impl;

import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.closeable.CloseableIterators;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.ss.SsTable;

public class EmptySsTable implements SsTable {

    @Override
    public CloseableIterator<Input> iterator(Input from, Input to) {
        return CloseableIterators.empty();
    }
}
