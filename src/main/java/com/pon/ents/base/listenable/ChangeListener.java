package com.pon.ents.base.listenable;

import javax.annotation.Nullable;

/**
 * A listener to changes of a non-{@literal null} value.
 */
public interface ChangeListener<T> {

    /**
     * Notifies that the value changed from {@code previous} to {@code current}.
     * <p>
     * A special case of initial notification provides a {@literal null} {@code previous} value.
     */
    void onChanged(@Nullable T previous, T current);
}
