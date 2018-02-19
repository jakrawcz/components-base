package com.pon.ents.base.serie;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.pon.ents.base.serie.impl.MergingIterator;

public abstract class SortedIterators {

    /**
     * Returns an {@link Iterator} that will provide all the elements provided by the given {@link Iterator}s, while
     * assuming they are (and should still be) sorted.
     */
    @SafeVarargs
    public static <T extends Comparable<T>> Iterator<T> mergingSorted(Iterator<? extends T>... iterators) {
        return mergingSorted(Comparator.naturalOrder(), iterators);
    }

    /**
     * Returns an {@link Iterator} that will provide all the elements provided by the given {@link Iterator}s, while
     * assuming they are (and should still be) sorted.
     */
    public static <T extends Comparable<T>> Iterator<T> mergingSorted(
            Collection<? extends Iterator<? extends T>> iterators) {
        return mergingSorted(Comparator.naturalOrder(), iterators);
    }

    /**
     * Returns an {@link Iterator} that will provide all the elements provided by the given {@link Iterator}s, while
     * assuming they are (and should still be) sorted according to the given {@link Comparator}.
     */
    @SafeVarargs
    public static <T> Iterator<T> mergingSorted(
            Comparator<? super T> comparator, Iterator<? extends T>... iterators) {
        return mergingSorted(comparator, Arrays.asList(iterators));
    }

    /**
     * Returns an {@link Iterator} that will provide all the elements provided by the given {@link Iterator}s, while
     * assuming they are (and should still be) sorted according to the given {@link Comparator}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> mergingSorted(
            Comparator<? super T> comparator, Collection<? extends Iterator<? extends T>> iterators) {
        switch (iterators.size()) {
            case 0: return Collections.emptyIterator();
            case 1: return (Iterator<T>) Iterables.getOnlyElement(iterators);
            default: return new MergingIterator<>(iterators.iterator(), comparator);
        }
    }
}
