package com.pon.ents.base.serie;

import java.util.function.Supplier;

import com.pon.ents.base.closeable.RuntimeCloseable;

/**
 * A {@link Supplier} of {@link Serie}s that wraps and forks some {@link Serie}.
 * <p>
 * Closing the {@link SerieForker} does not close the underlying {@link Serie}, but declares that no more opening
 * will occur and is strictly required.
 */
public interface SerieForker<T> extends Supplier<Serie<T>>, RuntimeCloseable {
}
