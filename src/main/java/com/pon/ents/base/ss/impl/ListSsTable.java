package com.pon.ents.base.ss.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpeners;
import com.pon.ents.base.io.InputOpeners.InputForker;
import com.pon.ents.base.serie.Serie;
import com.pon.ents.base.serie.Series;
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
    public Serie<Input> get(Input from, Input to) {
        int fromIndex = searchFor(from);
        if (fromIndex == inputSuppliers.size()) {
            return Series.empty();
        }
        int toIndex = searchFor(to);
        return Series.of(inputSuppliers.subList(fromIndex, toIndex)).transform(Supplier::get);
    }

    private int searchFor(Input input) {
        try (InputForker inputForker = InputOpeners.forkingForOneThread(input)) {
            int searchResult = Collections.binarySearch(inputSuppliers, inputForker, COMPARATOR);
            return searchResult < 0 ? -searchResult - 1 : searchResult;
        }
    }
}
