package com.pon.ents.base.closeable.impl;

import java.util.function.Consumer;

import com.pon.ents.base.closeable.CloseableConsumer;
import com.pon.ents.base.closeable.RuntimeCloseable;

public class CloseableConsumerAdapter<T> implements CloseableConsumer<T> {

    private final Consumer<T> consumer;
    private final RuntimeCloseable runtimeCloseable;

    public CloseableConsumerAdapter(Consumer<T> consumer, RuntimeCloseable runtimeCloseable) {
        this.consumer = consumer;
        this.runtimeCloseable = runtimeCloseable;
    }

    @Override
    public void accept(T t) {
        consumer.accept(t);
    }

    @Override
    public void close() {
        runtimeCloseable.close();
    }
}
