package com.pon.ents.base.reflector.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;

public abstract class Types {

    public static Type newParameterizedType(Class<?> rawClass, Type... typeArguments) {
        return new ParameterizedTypeImpl(rawClass, typeArguments);
    }

    public static WildcardType subtypeOf(Type bound) {
        return new WildcardTypeImpl(new Type[] {bound}, new Type[0]);
    }

    private static boolean typesEqual(Type a, Type b) {
        if (a == b) {
            return true;
        } else if (a instanceof Class) {
            return a.equals(b);
        } else if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType pa = (ParameterizedType) a;
            ParameterizedType pb = (ParameterizedType) b;
            return Objects.equal(pa.getOwnerType(), pb.getOwnerType()) && pa.getRawType().equals(pb.getRawType())
                    && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
        } else if (a instanceof GenericArrayType) {
            if (!(b instanceof GenericArrayType)) {
                return false;
            }
            GenericArrayType ga = (GenericArrayType) a;
            GenericArrayType gb = (GenericArrayType) b;
            return typesEqual(ga.getGenericComponentType(), gb.getGenericComponentType());

        } else if (a instanceof WildcardType) {
            if (!(b instanceof WildcardType)) {
                return false;
            }

            WildcardType wa = (WildcardType) a;
            WildcardType wb = (WildcardType) b;
            return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
                    && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

        } else if (a instanceof TypeVariable) {
            if (!(b instanceof TypeVariable)) {
                return false;
            }
            TypeVariable<?> va = (TypeVariable<?>) a;
            TypeVariable<?> vb = (TypeVariable<?>) b;
            return va.getGenericDeclaration().equals(vb.getGenericDeclaration()) && va.getName().equals(vb.getName());

        } else {
            return false;
        }
    }

    private static String typeToString(Type type) {
        return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
    }

    public static class ParameterizedTypeImpl implements ParameterizedType {

        private final Class<?> rawType;
        private final Type[] typeArguments;

        public ParameterizedTypeImpl(Class<?> rawType, Type... typeArguments) {
            for (Type typeArgument : typeArguments) {
                Preconditions.checkArgument(!Primitives.allPrimitiveTypes().contains(typeArgument));
            }
            this.rawType = rawType;
            this.typeArguments = typeArguments.clone();
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments.clone();
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ParameterizedType && typesEqual(this, (ParameterizedType) other);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(typeArguments) ^ rawType.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(30 * (typeArguments.length + 1));
            stringBuilder.append(typeToString(rawType));

            if (typeArguments.length == 0) {
                return stringBuilder.toString();
            }

            stringBuilder.append("<").append(typeToString(typeArguments[0]));
            for (int i = 1; i < typeArguments.length; i++) {
                stringBuilder.append(", ").append(typeToString(typeArguments[i]));
            }
            return stringBuilder.append(">").toString();
        }
    }

    public static class WildcardTypeImpl implements WildcardType {

        private final Type upperBound;

        @Nullable
        private final Type lowerBound;

        public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            checkArgument(lowerBounds.length <= 1, "Must have at most one lower bound.");
            checkArgument(upperBounds.length == 1, "Must have exactly one upper bound.");
            if (lowerBounds.length == 1) {
                this.lowerBound = lowerBounds[0];
                this.upperBound = Object.class;
                Preconditions.checkArgument(!Primitives.allPrimitiveTypes().contains(lowerBound));
            } else {
                this.lowerBound = null;
                this.upperBound = upperBounds[0];
                Preconditions.checkArgument(!Primitives.allPrimitiveTypes().contains(upperBound));
            }
        }

        @Override
        public Type[] getUpperBounds() {
            return new Type[] {upperBound};
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBound != null ? new Type[] {lowerBound} : new Type[0];
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof WildcardType && typesEqual(this, (WildcardType) other);
        }

        @Override
        public int hashCode() {
            return (lowerBound != null ? 31 + lowerBound.hashCode() : 1) ^ (31 + upperBound.hashCode());
        }

        @Override
        public String toString() {
            if (lowerBound != null) {
                return "? super " + typeToString(lowerBound);
            } else if (upperBound == Object.class) {
                return "?";
            } else {
                return "? extends " + typeToString(upperBound);
            }
        }
    }
}
