package com.pon.ents.base.executor.ex;

/**
 * TODO: document
 */
public class WaitInterruptedException extends RuntimeException {

    private static final long serialVersionUID = -6032474380004439466L;

    public WaitInterruptedException(InterruptedException e) {
        super(e);
    }
}
