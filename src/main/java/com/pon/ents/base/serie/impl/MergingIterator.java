package com.pon.ents.base.serie.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Streams;
import com.pon.ents.base.serie.Peekerator;
import com.pon.ents.base.serie.Peekerators;

public class MergingIterator<T> extends AbstractIterator<T> implements Iterator<T> {

    private final Topper<Peekerator<? extends T>> peekerators;

    public MergingIterator(Iterator<? extends Iterator<? extends T>> iterators, Comparator<? super T> comparator) {
        this(Toppers.forUpTo(peekerators(iterators), Comparator.comparing(Peekerator::current, comparator)));
    }

    private MergingIterator(Topper<Peekerator<? extends T>> peekerators) {
        this.peekerators = peekerators;
    }

    @Override
    public T computeNext() {
        @Nullable Peekerator<? extends T> lowestPeekerator = peekerators.removeTop();
        if (lowestPeekerator == null) {
            return endOfData();
        }
        T lowestObject = lowestPeekerator.current();
        if (lowestPeekerator.advance()) {
            peekerators.add(lowestPeekerator);
        }
        return lowestObject;
    }

    private static <T> Collection<Peekerator<? extends T>> peekerators(
            Iterator<? extends Iterator<? extends T>> iterators) {
        return Streams.stream(iterators)
                .filter(Iterator::hasNext)
                .map(Peekerators::from)
                .collect(Collectors.toList());
    }
}
