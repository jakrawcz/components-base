package com.pon.ents.base.io;

import com.pon.ents.base.closeable.RuntimeCloseable;
import com.pon.ents.base.io.impl.SingleThreadedInputForker;

public abstract class InputOpeners {

    /**
     * An {@link InputOpener} that wraps and forks some {@link Input}, and thus needs
     * {@link RuntimeCloseable#close() closing}.
     */
    public interface InputForker extends InputOpener, RuntimeCloseable {

        /**
         * Notifies this {@link InputForker} that all the {@link #open() opening} was already done.
         * <p>
         * This will be used by an implementation for performance enhancement, but it is perfectly legal to never call
         * this method.
         */
        void opened();
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
}
