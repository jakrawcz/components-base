package com.pon.ents.base.management.impl;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pon.ents.base.functional.MorePredicates;
import com.pon.ents.base.management.Manager;
import com.pon.ents.base.retry.Retries;
import com.pon.ents.base.serie.MoreStreams;

public class RetryingManager<K, T> implements Manager<K, T> {

    private static final Logger LOG = LoggerFactory.getLogger(RetryingManager.class);

    private final Manager<K, T> underlying;
    private final Predicate<Throwable> retryablePredicate;

    public RetryingManager(Manager<K, T> underlying, Predicate<Throwable> retryablePredicate) {
        this.underlying = underlying;
        this.retryablePredicate = retryablePredicate;
    }

    @Override
    public T access(K key) {
        return Retries.indefinite(
                () -> underlying.access(key),
                MorePredicates.afterAccepting(
                        retryablePredicate,
                        warnAboutIoErrorRetry("getting object for key " + key + " from " + underlying)));
    }

    @Override
    public Stream<K> streamNonEmptyObjectKeys() {
        return Retries.indefinite(
                () -> MoreStreams.materialize(underlying.streamNonEmptyObjectKeys()),
                MorePredicates.afterAccepting(
                        retryablePredicate,
                        warnAboutIoErrorRetry("opening stream of non-empty object keys of " + underlying)));
    }

    private static Consumer<Throwable> warnAboutIoErrorRetry(String actionDescription) {
        return e -> LOG.warn("a retryable problem occurred while " + actionDescription + ": " + e + "; re-trying");
    }
}
