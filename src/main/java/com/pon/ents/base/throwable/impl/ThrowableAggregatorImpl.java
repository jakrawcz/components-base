package com.pon.ents.base.throwable.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Throwables;
import com.pon.ents.base.throwable.ThrowableAggregator;

public class ThrowableAggregatorImpl implements ThrowableAggregator {

    private final List<Throwable> throwables;

    public ThrowableAggregatorImpl() {
        this.throwables = new ArrayList<>();
    }

    @Override
    public void aggregate(Throwable throwable) {
        throwables.add(throwable);
    }

    @Override
    public void close() {
        switch (throwables.size()) {
            case 0: return;
            case 1: {
                Throwable throwable = throwables.get(0);
                Throwables.throwIfUnchecked(throwable);
                throw new AggregateException(throwable);
            }
            default: throw new AggregateException(throwables);
        }
    }
}
