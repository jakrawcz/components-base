package com.pon.ents.base.closeable;

import java.util.function.Consumer;

import com.pon.ents.base.closeable.impl.CloseableConsumerAdapter;


public abstract class CloseableConsumers {

    /**
     * Returns a {@link CloseableConsumer} that does not really need closing (i.e. wraps the given regular
     * {@link Consumer}).
     */
    public static <T> CloseableConsumer<T> adapt(Consumer<T> consumer) {
        return adapt(consumer, RuntimeCloseables.noop());
    }

    /**
     * Returns a {@link CloseableConsumer} wrapper that delegates the closing to the given {@link RuntimeCloseable}.
     */
    private static <T> CloseableConsumer<T> adapt(Consumer<T> consumer, RuntimeCloseable runtimeCloseable) {
        return new CloseableConsumerAdapter<>(consumer, runtimeCloseable);
    }
}
