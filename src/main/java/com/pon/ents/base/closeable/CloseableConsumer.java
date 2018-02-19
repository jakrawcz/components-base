package com.pon.ents.base.closeable;

import java.util.function.Consumer;

/**
 * A {@link RuntimeCloseable closeable} {@link Consumer}.
 */
public interface CloseableConsumer<T> extends Consumer<T>, RuntimeCloseable {
}
