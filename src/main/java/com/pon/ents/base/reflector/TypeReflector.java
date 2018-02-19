package com.pon.ents.base.reflector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.pon.ents.base.reflector.impl.DirectTypeReflector;
import com.pon.ents.base.reflector.impl.Types;

/**
 * A convenient API of a fully-specified {@link Type}.
 */
public interface TypeReflector {

    /**
     * Returns the same
     */
    default TypeReflector withTypeArguments(TypeReflector... typeArguments) {
        Class<?> rawClass = raw();
        TypeVariable<?>[] typeParameters = rawClass.getTypeParameters();
        Preconditions.checkArgument(typeParameters.length == typeArguments.length,
                "type arguments (%s) do not match type parameters (%s)",
                Arrays.asList(typeArguments), Arrays.asList(typeParameters));
        return new DirectTypeReflector(Types.newParameterizedType(
                rawClass, Stream.of(typeArguments).map(TypeReflector::unwrap).toArray(Type[]::new)));
    }

    /**
     * Streams actual type arguments.
     * <p>
     * This may be an empty {@link Stream} (for non-parameterized {@link Class}es) or a {@link Stream} of upper bounds
     * of all type parameters (for parameterized {@link Class}es that were passed raw).
     */
    default Stream<TypeReflector> streamTypeArguments() {
        Type type = unwrap();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return Stream.of(parameterizedType.getActualTypeArguments()).map(DirectTypeReflector::new);
        } else if (type instanceof Class) {
            Class<?> rawClass = (Class<?>) type;
            return Stream.of(rawClass.getTypeParameters()).map(TypeReflectors::upperBoundOf);
        } else if (type instanceof WildcardType) {
            return Stream.empty();
        }
        throw new AssertionError();
    }

    /**
     * Returns an underlying {@link Type}.
     */
    Type unwrap();

    /**
     * Returns a raw {@link Class}.
     */
    @SuppressWarnings("unchecked")
    default <T> Class<T> raw() {
        return (Class<T>) TypeToken.of(unwrap()).getRawType();
    }

    /**
     * Streams the {@code extends} chain.
     */
    default Stream<TypeReflector> streamSuperclassHierarchy() {
        TypeToken<?> typeToken = TypeToken.of(unwrap());
        @Nullable Class<?> superclass = raw().getSuperclass();
        List<TypeReflector> chain = new ArrayList<>();
        while (superclass != null) {
            chain.add(TypeReflectors.of(typeToken.resolveType(superclass).getType()));
        }
        return chain.stream();
    }

    /**
     * Streams all actual implemented interfaces.
     */
    default Stream<TypeReflector> streamImplementedInterfaces() {
        TypeToken<?> typeToken = TypeToken.of(unwrap());
        return Stream.concat(Stream.of(this), streamSuperclassHierarchy())
                .flatMap(typeReflector -> Stream.of(typeReflector.raw().getInterfaces())
                        .map(rawInterface -> TypeReflectors.of(typeToken.resolveType(rawInterface).getType())));
    }
}
