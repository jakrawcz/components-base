package com.pon.ents.base.functional;

import java.util.function.Supplier;

public abstract class Suppliers {

    /**
     * Returns a unique {@link Supplier} of the always-the-same given object.
     */
    public static <T> Supplier<T> of(T object) {
        return () -> object;
    }
}
