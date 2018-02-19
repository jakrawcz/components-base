package com.pon.ents.base.retry;

import java.util.concurrent.Callable;

/**
 * A {@link Callable} counterpart that throws a {@link Throwable} of a specific type.
 */
public interface Attempt<T, X extends Throwable> {

    /**
     * Returns the result of a single {@link Attempt}, or throws a {@link Throwable}.
     */
    T call() throws X;
}
