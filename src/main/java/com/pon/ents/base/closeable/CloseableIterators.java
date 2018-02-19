package com.pon.ents.base.closeable;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.Iterators;
import com.pon.ents.base.closeable.impl.CloseableIteratorAdapter;
import com.pon.ents.base.runnable.Runnables;

public abstract class CloseableIterators {

    /**
     * A {@link CloseableIterator}-enabled counterpart of {@link Iterators#transform}.
     */
    public static <F, T> CloseableIterator<T> transform(CloseableIterator<F> iterator, Function<F, T> function) {
        return adapt(Iterators.transform(iterator, function::apply), iterator::close);
    }

    /**
     * A {@link CloseableIterator}-enabled counterpart of {@link Iterators#filter}.
     */
    public static <T> CloseableIterator<T> filter(CloseableIterator<T> iterator, Predicate<? super T> predicate) {
        return adapt(Iterators.filter(iterator, predicate::test), iterator::close);
    }

    /**
     * A {@link CloseableIterator}-enabled counterpart of {@link Collections#emptyIterator}.
     */
    public static <T> CloseableIterator<T> empty() {
        return adapt(Collections.emptyIterator());
    }

    /**
     * Returns a {@link CloseableIterator} that does not really need closing.
     */
    public static <T> CloseableIterator<T> adapt(Iterator<T> iterator) {
        return adapt(iterator, Runnables.noop());
    }

    /**
     * Returns a {@link CloseableIterator} that uses an external closing logic.
     */
    public static <T> CloseableIterator<T> adapt(Iterator<T> iterator, Runnable closingTask) {
        return new CloseableIteratorAdapter<>(iterator, closingTask);
    }
}
