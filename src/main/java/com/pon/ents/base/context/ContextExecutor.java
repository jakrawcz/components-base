package com.pon.ents.base.context;

import com.pon.ents.base.closeable.RuntimeCloseable;

/**
 * TODO: document
 */
public interface ContextExecutor<T> extends RuntimeCloseable {

    void execute(ContextRunnable<T> contextRunnable);
}
