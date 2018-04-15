package com.pon.ents.base.serie;

import com.pon.ents.base.serie.impl.SingleThreadedSerieForker;

public abstract class SerieForkers {

    /**
     * Returns a single-threaded {@link SerieForker forker} of the given {@link Serie}.
     */
    public static <T> SerieForker<T> singleThreadedForkerOf(Serie<T> serie) {
        return new SingleThreadedSerieForker<>(serie);
    }
}
