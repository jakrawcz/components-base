package com.pon.ents.base.serie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.serie.impl.FilteringSerie;
import com.pon.ents.base.serie.impl.SerieIterator;
import com.pon.ents.base.serie.impl.TransformingSerie;

/**
 * An {@link Iterator} counterpart that is aware of underlying resources (i.e. needs {@link RuntimeCloseable closing})
 * and of the number of {@link #remaining()} elements; this can also be viewed as an {@link Input} counterpart, but for
 * non-{@literal null} elements of a known type {@code T} (rather than for bytes).
 * <p>
 * This interface is {@link RuntimeCloseable}, but needs {@link RuntimeCloseable#close() closing} only in the situations
 * where it is not consumed until the end (i.e. until any "get" operation returns {@literal null}). If this happens, the
 * {@link Serie} is assumed to have released its resources anyway, and {@link #close()} is legal, but no-op.
 * <p>
 * This interface is {@link Iterable}, for the convenience of the for-each syntax, but in fact should be considered
 * "iterable once", i.e. it is possible to transform it into an {@link Iterator}. Please be aware that the original
 * {@link Serie} will still need to be {@link RuntimeCloseable#close() closed} if the {@link Iterator} is not fully
 * consumed.
 */
public interface Serie<T> extends RuntimeCloseable, Iterable<T> {

    /**
     * Returns the next element, or {@literal null} if the end was reached.
     */
    @Nullable
    T next();

    /**
     * Returns an exact number of remaining elements, or -1 if unknown (or e.g. infinite).
     * <p>
     * If at any point this method returns an exact number, then all future calls will return the exact number as well
     * (and precisely the one that is smaller from the previously returned by the number of elements got since then; in
     * other words: the implementation will track the known value accurately and will never turn it "unknown").
     */
    long remaining();

    @Override
    default Iterator<T> iterator() {
        return new SerieIterator<>(this);
    }

    /**
     * A counterpart of {@link Iterators#transform}.
     */
    default <N> Serie<N> transform(Function<? super T, ? extends N> function) {
        return new TransformingSerie<>(this, function);
    }

    /**
     * A counterpart of {@link Iterators#filter}.
     */
    default Serie<T> filter(Predicate<? super T> predicate) {
        return new FilteringSerie<>(this, predicate);
    }

    /**
     * Collects all the remaining elements to a {@link List} and {@link #close() closes} (which technically should not
     * be needed).
     */
    default List<T> toList() {
        try {
            long remaining = remaining();
            if (remaining == -1) {
                return Lists.newArrayList(iterator());
            }
            List<T> list = new ArrayList<>(Ints.checkedCast(remaining));
            Iterators.addAll(list, iterator());
            return list;
        } finally {
            close();
        }
    }
}
