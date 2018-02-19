package com.pon.ents.base.listenable;

import com.pon.ents.base.closeable.RuntimeCloseable;

/**
 * A {@link RuntimeCloseable} that unregisters a specific listener on {@link #close()}.
 */
public interface ListenerRegistration extends RuntimeCloseable {

    /**
     * An alias for {@link #close()}.
     */
    default void unregister() {
        close();
    }
}
