package com.pon.ents.base.serie;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MoreStreams {

    /**
     * Returns a {@link Stream} that is guaranteed to not throw any {@link Throwable}, not block and not need
     * {@link Stream#close() closing}, and will provide all the elements provided by the given one.
     * <p>
     * This is achieved simply by buffering all elements of the given {@link Stream} prior to returning. Thus, it should
     * not be used with {@link Stream}s that operate in "cursor" mode (or in any way "expire" previous elements) or
     * that provide unlimited/very large number of elements.
     */
    public static <T> Stream<T> materialize(Stream<T> stream) {
        return stream.collect(Collectors.toList()).stream();
    }

}
