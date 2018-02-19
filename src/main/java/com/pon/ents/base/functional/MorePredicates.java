package com.pon.ents.base.functional;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class MorePredicates {

    /**
     * Returns a {@link Predicate} that accepts instances of the given {@link Class}.
     */
    public static <T> Predicate<T> isInstanceOf(Class<? extends T> cls) {
        return cls::isInstance;
    }

    /**
     * Returns a {@link Predicate} that will additionally {@link Consumer consume} every element that is accepted by the
     * given {@link Predicate}.
     * <p>
     * This is in general discouraged; the {@link Consumer} should not have a side-effect.
     */
    public static <T> Predicate<T> afterAccepting(Predicate<T> underlying, Consumer<? super T> consumer) {
        return element -> {
            if (underlying.test(element)) {
                consumer.accept(element);
                return true;
            } else {
                return false;
            }
        };
    }

    /**
     * Returns a {@link Predicate} that will additionally {@link Consumer consume} every element before testing it using
     * the given {@link Predicate}.
     * <p>
     * This is in general discouraged; the {@link Consumer} should not have a side-effect.
     */
    public static <T> Predicate<T> beforeTesting(Consumer<? super T> consumer, Predicate<T> underlying) {
        return element -> {
            consumer.accept(element);
            return underlying.test(element);
        };
    }
}
