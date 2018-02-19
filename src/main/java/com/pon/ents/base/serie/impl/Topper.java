package com.pon.ents.base.serie.impl;

import javax.annotation.Nullable;

public interface Topper<T> {

    void add(T object);

    @Nullable
    T removeTop();
}
