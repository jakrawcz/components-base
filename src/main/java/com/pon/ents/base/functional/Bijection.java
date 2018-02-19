package com.pon.ents.base.functional;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.pon.ents.base.functional.impl.IdentityBijection;
import com.pon.ents.base.functional.impl.Inverse;

/**
 * A two-way counterpart of a {@link Function}.
 */
public interface Bijection<A, B> {

    Bijection<Object, Object> IDENTITY = new IdentityBijection<>();

    /**
     * Maps A to B.
     */
    @Nullable
    B forward(@Nullable A a);

    /**
     * Maps B to A.
     */
    @Nullable
    A backward(@Nullable B b);

    /**
     * Returns an inverse {@link Bijection}.
     */
    default Bijection<B, A> invert() {
        return new Inverse<>(this);
    }

    /**
     * Returns an identity {@link Bijection}.
     */
    @SuppressWarnings("unchecked")
    static <T> Bijection<T, T> identity() {
        return (Bijection<T, T>) IDENTITY;
    }
}
