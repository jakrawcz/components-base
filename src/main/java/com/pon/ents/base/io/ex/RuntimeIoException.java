package com.pon.ents.base.io.ex;

import java.io.IOException;

/**
 * TODO: document
 */
public class RuntimeIoException extends RuntimeException {

    private static final long serialVersionUID = -5544916869900133904L;

    public RuntimeIoException() {
    }

    public RuntimeIoException(String message) {
        super(message);
    }

    public RuntimeIoException(IOException cause) {
        super(cause);
    }
}
