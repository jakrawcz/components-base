package com.pon.ents.base.serie;

import java.util.Iterator;

/**
 * A counterpart of a necessarily non-empty {@link Iterator} with a different approach to moving through elements (most
 * notably - with an ability to {@link #current() peek} at the current element).
 */
public interface Peekerator<T> {

    /**
     * Returns an element which this {@link Peekerator} is peeking at.
     * <p>
     * Please note that this API requires that a {@link Peekerator} may never represent an empty series of elements.
     */
    T current();

    /**
     * Advances to the next element and returns {@literal true}, or {@literal false} if it is not possible (in which
     * case the {@link #current() current element} will remain the same).
     */
    boolean advance();
}
