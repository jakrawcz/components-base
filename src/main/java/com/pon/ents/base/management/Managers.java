package com.pon.ents.base.management;

import java.util.function.Function;
import java.util.function.Predicate;

import com.pon.ents.base.functional.Bijection;
import com.pon.ents.base.management.impl.ConvertingManager;
import com.pon.ents.base.management.impl.RetryingManager;

public class Managers {

    /**
     * Returns a {@link Manager} that will pass all the keys through the given {@link Bijection}.
     */
    public static <FK, TK, T> Manager<TK, T> keyConverting(Manager<FK, T> underlying, Bijection<FK, TK> keyConverter) {
        return new ConvertingManager<>(underlying, keyConverter, Function.identity());
    }

    /**
     * Returns a {@link Manager} that will pass all the objects through the given {@link Function}.
     */
    public static <K, FT, TT> Manager<K, TT> objectConverting(
            Manager<K, FT> underlying, Function<FT, TT> objectConverter) {
        return new ConvertingManager<>(underlying, Bijection.identity(), objectConverter);
    }

    /**
     * Returns a {@link Manager} that will retry (after logging a warning) any access that throws a {@link Throwable}
     * accepted by the given {@link Predicate} (and, obviously, propagate those not accepted).
     */
    public static <K, T> Manager<K, T> retrying(Manager<K, T> underlying, Predicate<Throwable> retryablePredicate) {
        return new RetryingManager<>(underlying, retryablePredicate);
    }
}
