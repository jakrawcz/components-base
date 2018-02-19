package com.pon.ents.base.text;

import com.pon.ents.base.functional.Bijection;
import com.pon.ents.base.proxy.Proxies;

public abstract class Repars {

    private static final Repar<String> IDENTITY = adapt(Bijection.identity());

    /**
     * Returns a {@link Repar} of {@link String}s (i.e. doing nothing).
     */
    public static Repar<String> identity() {
        return IDENTITY;
    }

    /**
     * Wraps a {@link Bijection} into a {@link Repar}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Repar<T> adapt(Bijection<T, String> stringConverter) {
        return Proxies.wrapping(stringConverter, Repar.class);
    }
}
