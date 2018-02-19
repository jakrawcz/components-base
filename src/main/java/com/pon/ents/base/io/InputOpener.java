package com.pon.ents.base.io;

import com.pon.ents.base.closeable.Opener;
import com.pon.ents.base.io.ex.RuntimeEofException;

/**
 * An {@link Opener} of {@link Input}s with additional features.
 */
public interface InputOpener extends Opener<Input> {

    /**
     * Opens an {@link Input} at the given offset.
     * <p>
     * This should be more performant than {@link #open() opening} and {@link Input#skipFully(long) skipping} (and also
     * will throw {@link IllegalArgumentException}s instead of {@link RuntimeEofException}s in case offset is too high).
     */
    Input openAt(long offset);

    @Override
    default Input open() {
        return openAt(0);
    }
}
