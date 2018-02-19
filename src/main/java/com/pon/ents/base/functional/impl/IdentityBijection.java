package com.pon.ents.base.functional.impl;

import com.pon.ents.base.functional.Bijection;

public class IdentityBijection<T> implements Bijection<T, T> {

    @Override
    public T forward(T a) {
        return a;
    }

    @Override
    public T backward(T b) {
        return b;
    }
}
