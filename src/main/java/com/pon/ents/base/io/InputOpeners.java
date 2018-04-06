package com.pon.ents.base.io;

import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.io.impl.IoBufferInputOpener;
import com.pon.ents.base.io.impl.SingleThreadedInputForker;

public abstract class InputOpeners {

    /**
     * An {@link InputOpener} that wraps and forks some {@link Input}.
     * <p>
     * Closing the {@link InputForker} does not close the underlying {@link Input}, but declares that no more opening
     * will occur and is strictly required.
     */
    public interface InputForker extends InputOpener, RuntimeCloseable {
    }

    /**
     * Returns a thread-safe {@link InputForker} that will open {@link Input}s with contents identical to the given one.
     */
    public static InputForker forking(Input input) {
        throw new UnsupportedOperationException("not implemented yet"); // TODO: FEATURE: implement
    }

    /**
     * Returns a non-thread-safe version of a {@link #forking(Input) forker}.
     * <p>
     * Not only the {@link InputForker} methods are non-thread-safe, but all access to the returned {@link Input}s
     * as well.
     */
    public static InputForker forkingForOneThread(Input input) {
        return new SingleThreadedInputForker(input);
    }

    /**
     * Returns an {@link InputOpener} that will use the given {@link IoBuffer}.
     */
    public static InputOpener ofIoBuffer(IoBuffer ioBuffer) {
        return new IoBufferInputOpener(ioBuffer);
    }
}
