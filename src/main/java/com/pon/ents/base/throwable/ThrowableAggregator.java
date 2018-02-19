package com.pon.ents.base.throwable;

import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.throwable.impl.AggregateException;

/**
 * A suppressor/re-thrower of a single (as-is) or many (as {@link AggregateException}) {@link Throwable}s.
 */
public interface ThrowableAggregator extends RuntimeCloseable {

    /**
     * Suppresses the given {@link Throwable} until {@link #close}d.
     */
    void aggregate(Throwable throwable);
}
