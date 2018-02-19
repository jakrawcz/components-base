package com.pon.ents.base.listenable;

import com.pon.ents.base.listenable.impl.SettableListenableImpl;

public abstract class Listenables {

    /**
     * Creates a {@link SettableListenable} with the given initial value.
     */
    public static <T> SettableListenable<T> settable(T initialValue) {
        return new SettableListenableImpl<>(initialValue);
    }
}
