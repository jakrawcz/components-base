package com.pon.ents.base.functional.impl;

import com.pon.ents.base.functional.Bijection;

public class Inverse<A, B> implements Bijection<B, A> {

    private final Bijection<A, B> underlying;

    public Inverse(Bijection<A, B> underlying) {
        this.underlying = underlying;
    }

    @Override
    public A forward(B b) {
        return underlying.backward(b);
    }

    @Override
    public B backward(A a) {
        return underlying.forward(a);
    }

    @Override
    public Bijection<A, B> invert() {
        return underlying; // clever, right?
    }
}
