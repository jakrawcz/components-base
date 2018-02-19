package com.pon.ents.base.retry;

import java.util.function.Predicate;

import com.pon.ents.base.functional.MorePredicates;

public abstract class Retries {

    public static <T, X extends Throwable> T indefinite(
            Attempt<T, X> attempt,
            Predicate<Throwable> retryablePredicate) throws X {
        while (true) {
            try {
                return attempt.call();
            } catch (Throwable t) {
                if (!retryablePredicate.test(t)) {
                    throw t;
                }
            }
        }
    }

    /**
     * TODO: document
     */
    public static <T, X extends Throwable> T indefinite(Attempt<T, X> attempt, Class<X> throwableClass) throws X {
        return indefinite(attempt, MorePredicates.isInstanceOf(throwableClass));
    }
}
