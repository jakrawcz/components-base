package com.pon.ents.base.serie;

import java.util.Iterator;

import com.google.common.base.Preconditions;
import com.pon.ents.base.serie.impl.IteratingPeekerator;

public abstract class Peekerators {

    /**
     * Adapts the non-empty {@link Iterator} into a {@link Peekerator}.
     */
    public static <T> Peekerator<T> from(Iterator<T> iterator) {
        Preconditions.checkArgument(iterator.hasNext(), "a peekerator cannot be empty");
        return new IteratingPeekerator<>(iterator.next(), iterator);
    }

    /**
     * Adapts the current element and the {@link Iterator} over the rest of elements (which may be empty) into a
     * {@link Peekerator}.
     */
    public static <T> Peekerator<T> from(T current, Iterator<T> rest) {
        return new IteratingPeekerator<>(current, rest);
    }
}
