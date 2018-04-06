package com.pon.ents.base.ss.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.Iterators;
import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.closeable.CloseableIterators;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpeners;
import com.pon.ents.base.io.InputOpeners.InputForker;
import com.pon.ents.base.ss.SortedInputs;
import com.pon.ents.base.ss.SsTable;

public class ListSsTable implements SsTable {

    private static final Comparator<? super Supplier<Input>> COMPARATOR =
            Comparator.comparing(Supplier::get, SortedInputs::compare);

    private final List<? extends Supplier<Input>> inputSuppliers;

    public ListSsTable(List<? extends Supplier<Input>> inputSuppliers) {
        this.inputSuppliers = inputSuppliers;
    }

    @Override
    public CloseableIterator<Input> iterator(Input from, Input to) {
        int fromIndex = searchFor(from);
        if (fromIndex == inputSuppliers.size()) {
            return CloseableIterators.empty();
        }
        int toIndex = searchFor(to);
        return CloseableIterators.adapt(Iterators.transform(
                inputSuppliers.subList(fromIndex, toIndex).iterator(), Supplier::get));
    }

    private int searchFor(Input input) {
        try (InputForker inputForker = InputOpeners.forkingForOneThread(input)) {
            int searchResult = Collections.binarySearch(inputSuppliers, inputForker, COMPARATOR);
            return searchResult < 0 ? -searchResult - 1 : searchResult;
        }
    }
}
