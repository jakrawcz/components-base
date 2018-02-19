package com.pon.ents.base.reflector;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.reflector.impl.DirectTypeReflector;
import com.pon.ents.base.reflector.impl.Types;

public abstract class TypeReflectors {

    /**
     * Returns a {@link TypeReflector} of the given {@link Type} (which needs to be fully specified in advance).
     */
    public static TypeReflector of(Type fullySpecifiedType) {
        return new DirectTypeReflector(fullySpecifiedType);
    }

    /**
     * Returns a {@link TypeReflector} of an upper bound the given {@link TypeVariable}.
     */
    public static TypeReflector upperBoundOf(TypeVariable<?> typeVariable) {
        Type[] upperBounds = typeVariable.getBounds();
        Preconditions.checkArgument(upperBounds.length == 1, "unsupported type variable: %s", typeVariable);
        return of(Types.subtypeOf(upperBounds[0]));
    }
}
