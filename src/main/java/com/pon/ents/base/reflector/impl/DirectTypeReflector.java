package com.pon.ents.base.reflector.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.pon.ents.base.reflector.TypeReflector;

public class DirectTypeReflector implements TypeReflector {

    private final Type type;

    public DirectTypeReflector(Type type) {
        Preconditions.checkArgument(isFullySpecified(type), "%s is not fully specified", type);
        this.type = type;
    }

    @Override
    public Type unwrap() {
        return type;
    }

    private static boolean isFullySpecified(Type type) {
        if (type instanceof Class) {
            return true;
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            return wildcardType.getLowerBounds().length == 0;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return Stream.of(parameterizedType.getActualTypeArguments())
                    .allMatch(DirectTypeReflector::isFullySpecified);
        }
        return false;
    }
}
