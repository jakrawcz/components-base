package com.pon.ents.base.executor;

import java.util.concurrent.Executor;

import com.pon.ents.base.closeable.RuntimeCloseable;

/**
 * An {@link Executor} with a graceful {@link RuntimeCloseable#close()} operation that will stop accepting new commands
 * and await indefinitely until the running ones finish (or is interrupted).
 * <p>
 * It is illegal to {@link Executor#execute(Runnable) submit} new commands to a closed executor.
 */
public interface CloseableExecutor extends Executor, RuntimeCloseable {
}
