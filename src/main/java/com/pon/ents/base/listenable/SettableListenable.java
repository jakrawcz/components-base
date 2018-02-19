package com.pon.ents.base.listenable;

import java.util.function.UnaryOperator;

/**
 * A {@link Listenable} that can be manually {@link #set}.
 */
public interface SettableListenable<T> extends Listenable<T> {

    /**
     * Sets the new value while notifying all listeners before returning.
     */
    void set(T newValue);

    /**
     * Performs a {@link #get} and {@link #set}.
     */
    default void update(UnaryOperator<T> operator) {
        set(operator.apply(get()));
    }
}
