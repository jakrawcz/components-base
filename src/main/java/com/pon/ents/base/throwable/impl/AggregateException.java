package com.pon.ents.base.throwable.impl;

public class AggregateException extends RuntimeException {

    private static final long serialVersionUID = -214801422278335912L;

    public AggregateException(Throwable throwable) {
        super("an exception was thrown from a place that could not break the control flow", throwable);
    }

    public AggregateException(Iterable<Throwable> throwables) {
        super("multiple exceptions were thrown (please see suppressed)");
        for (Throwable throwable : throwables) {
            addSuppressed(throwable);
        }
    }
}
