package com.pon.ents.base.throwable;

import com.pon.ents.base.throwable.impl.ThrowableAggregatorImpl;

public abstract class MoreThrowables {

    /**
     * Opens a {@link ThrowableAggregator}.
     */
    public static ThrowableAggregator aggregate() {
        return new ThrowableAggregatorImpl();
    }
}
