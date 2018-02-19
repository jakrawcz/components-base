package com.pon.ents.base.functional;

import java.util.function.Consumer;

import com.pon.ents.base.functional.impl.IdempotentConsumer;
import com.pon.ents.base.functional.impl.OnlyOnceConsumer;

public abstract class Consumers {

    /**
     * Returns a wrapper that will forward to the given {@link Consumer} only once (thread-safely), ignoring any other
     * {@link Consumer#accept(Object)} calls.
     */
    public static <T> Consumer<T> idempotent(Consumer<T> consumer) {
        return new IdempotentConsumer<>(consumer);
    }

    /**
     * Returns a wrapper that will forward to the given {@link Consumer} only once (thread-safely).
     * <p>
     * It is illegal to call {@link Consumer#accept(Object)} again.
     */
    public static <T> Consumer<T> onlyOnce(Consumer<T> consumer) {
        return new OnlyOnceConsumer<>(consumer);
    }
}
