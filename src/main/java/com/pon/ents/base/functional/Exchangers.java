package com.pon.ents.base.functional;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.pon.ents.base.functional.impl.ConcurrentExpecter;
import com.pon.ents.base.functional.impl.Expecter;

public abstract class Exchangers {

    /**
     * Returns a non-concurrent "result-expecting" {@link Exchanger} that will accept {@link Consumer#accept} exactly
     * one element and only then will allow to {@link Supplier#get()} it exactly once.
     */
    public static <T> Exchanger<T> sameThreadExpecter() {
        return new Expecter<>();
    }

    /**
     * Returns a concurrent version of {@link #sameThreadExpecter()}.
     */
    public static <T> Exchanger<T> concurrentExpecter() {
        return new ConcurrentExpecter<>();
    }
}
