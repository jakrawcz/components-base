package com.pon.ents.base.serie.impl;

import java.util.Collection;
import java.util.Comparator;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public abstract class Toppers {

    public static <T> Topper<T> forUpTo(Collection<T> elements, Comparator<? super T> comparator) {
        switch (elements.size()) {
            case 0: return zeroCapacity();
            case 1: return singleton(Iterables.getOnlyElement(elements));
            case 2: return alternating(elements.iterator().next(), Iterables.getLast(elements), comparator);
            default: return tree(elements, comparator);
        }
    }

    public static <T> Topper<T> unbounded(Comparator<? super T> comparator) {
        return new PriorityQueueTopper<>(comparator);
    }

    private static <T> Topper<T> alternating(T first, T second, Comparator<? super T> comparator) {
        AlternatingTopper<T> topper = new AlternatingTopper<>(comparator);
        topper.add(first);
        topper.add(second);
        return topper;
    }

    private static <T> Topper<T> singleton(T element) {
        Preconditions.checkNotNull(element);
        return new Topper<T>() {

            @Nullable
            private T singleton = element;

            @Override
            public void add(T object) {
                Preconditions.checkNotNull(object);
                Preconditions.checkState(singleton == null, "full");
                this.singleton = object;
            }

            @Override
            @Nullable
            public T removeTop() {
                T top = singleton;
                this.singleton = null;
                return top;
            }
        };
    }

    private static <T> Topper<T> tree(Collection<T> elements, Comparator<? super T> comparator) {
        PriorityQueueTopper<T> treeTopper = new PriorityQueueTopper<>(comparator);
        for (T element : elements) {
            treeTopper.add(element);
        }
        return treeTopper;
    }

    private static <T> Topper<T> zeroCapacity() {
        return new Topper<T>() {

            @Override
            public void add(T object) {
                throw new IllegalStateException("no capacity");
            }

            @Override
            @Nullable
            public T removeTop() {
                return null;
            }
        };
    }

}
