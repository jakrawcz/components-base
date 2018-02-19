package com.pon.ents.base.equivalence;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;

public abstract class Equivalences {

    /**
     * Returns an {@link Equivalence#identity()} {@link Wrapper}.
     */
    public static <T> Wrapper<T> identityOf(T object) {
        return Equivalence.identity().wrap(object);
    }
}
