package com.pon.ents.base.serie;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

import com.google.common.collect.Collections2;
import com.pon.ents.base.closeable.RuntimeCloseables;
import com.pon.ents.base.runnable.Runnables;
import com.pon.ents.base.serie.impl.EmptySerie;
import com.pon.ents.base.serie.impl.ExternallyCloseableIteratorSerie;

public abstract class Series {

    /**
     * A {@link Serie} counterpart of {@link Collections#emptyIterator}.
     */
    public static <T> Serie<T> empty() {
        return new EmptySerie<>();
    }

    /**
     * Wraps a {@link Stream}.
     */
    public static <T> Serie<T> adapt(Stream<T> stream) {
        Spliterator<T> spliterator = stream.spliterator();
        return adapt(Spliterators.iterator(spliterator), spliterator.getExactSizeIfKnown(), stream::close);
    }

    /**
     * Wraps an unknown-size {@link Iterator} that does not really need closing.
     */
    public static <T> Serie<T> adapt(Iterator<T> iterator) {
        return adapt(iterator, Runnables.noop());
    }

    /**
     * Wraps an unknown-size {@link Iterator} together with an external {@link Runnable closing logic}.
     */
    public static <T> Serie<T> adapt(Iterator<T> iterator, Runnable closingTask) {
        return adapt(iterator, -1, closingTask);
    }

    /**
     * Wraps a known-size {@link Iterator} that does not really need closing.
     */
    private static <T> Serie<T> adapt(Iterator<T> iterator, long size) {
        return adapt(iterator, size, Runnables.noop());
    }

    /**
     * Wraps a known-size {@link Iterator} together with an external {@link Runnable closing logic}.
     */
    public static <T> Serie<T> adapt(Iterator<T> iterator, long size, Runnable closingTask) {
        return new ExternallyCloseableIteratorSerie<>(iterator, size, closingTask);
    }

    /**
     * Returns a {@link Serie} that contains the given elements.
     */
    @SafeVarargs
    public static <T> Serie<T> of(T... elements) {
        return of(Arrays.asList(elements));
    }

    /**
     * Returns a {@link Serie} that contains the given elements, assuming that the state of the {@link Collection} will
     * not mutate after calling this method (and most importantly - the {@link Collection#size()} will not change).
     */
    public static <T> Serie<T> of(Collection<T> elements) {
        return adapt(elements.iterator(), elements.size());
    }

    /**
     * A {@link SortedIterators#mergingSorted(Comparator, Collection)} counterpart for {@link Serie}s.
     */
    public static <T> Serie<T> mergingSorted(
            Comparator<? super T> comparator, Collection<? extends Serie<? extends T>> series) {
        Collection<Iterator<? extends T>> iterators = Collections2.transform(series, Serie::iterator);
        Iterator<T> iterator = SortedIterators.mergingSorted(comparator, iterators);
        return adapt(iterator, RuntimeCloseables.composite(series));
    }
}
