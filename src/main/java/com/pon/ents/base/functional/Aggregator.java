package com.pon.ents.base.functional;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A simultaneous {@link Consumer} and {@link Supplier}.
 * <p>
 * This is a pretty generic contract (well, because the same is true for both of the extended interfaces), and the
 * actual responsibility must be defined on a per-usage basis.
 */
public interface Aggregator<I, O> extends Consumer<I>, Supplier<O> {
}
