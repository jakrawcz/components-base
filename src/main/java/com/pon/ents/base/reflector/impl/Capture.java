package com.pon.ents.base.reflector.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class Capture<T> {

    private final Type type;

    protected Capture() {
        Type superclass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedSuperclass = (ParameterizedType) superclass;
        this.type = parameterizedSuperclass.getActualTypeArguments()[0];
    }

    public Type type() {
        return type;
    }
}
