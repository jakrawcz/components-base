package com.pon.ents.base.ss.impl;

import java.util.Iterator;

import com.pon.ents.base.io.Input;

public interface SsIndex {

    public interface SsIndexEntry {

        Input readPrefix();

        long offset();
    }

    Input readFirst();

    Iterator<SsIndexEntry> intermediateEntryIterator();

    Input readLast();
}
