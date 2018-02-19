package com.pon.ents.base.functional;

/**
 * An {@link Aggregator} that accepts and supplies items of the same type.
 */
public interface Exchanger<T> extends Aggregator<T, T> {
}
