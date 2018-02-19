package com.pon.ents.base.io.impl;

import javax.annotation.Nullable;

import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.io.Input;

public class ConcatenatingInput implements Input {

    private final CloseableIterator<Input> inputCloseableIterator;

    @Nullable
    private Input currentInput;

    public ConcatenatingInput(CloseableIterator<Input> inputCloseableIterator) {
        this.inputCloseableIterator = inputCloseableIterator;
        this.currentInput = null;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        while (true) {
            if (currentInput == null) {
                if (!inputCloseableIterator.hasNext()) {
                    return -1;
                }
                this.currentInput = inputCloseableIterator.next();
            }
            int read = currentInput.read(buffer, offset, length);
            if (read != -1) {
                return read;
            }
            this.currentInput = null;
        }
    }

    @Override
    public int read() {
        while (true) {
            if (currentInput == null) {
                if (!inputCloseableIterator.hasNext()) {
                    return -1;
                }
                this.currentInput = inputCloseableIterator.next();
            }
            int read = currentInput.read();
            if (read != -1) {
                return read;
            }
            this.currentInput = null;
        }
    }

    @Override
    public long remaining() {
        if (inputCloseableIterator.hasNext()) {
            if (currentInput == null) {
                this.currentInput = inputCloseableIterator.next();
                if (inputCloseableIterator.hasNext()) {
                    return -1;
                } else {
                    return currentInput.remaining();
                }
            } else {
                return -1;
            }
        } else {
            if (currentInput == null) {
                return 0;
            } else {
                return currentInput.remaining();
            }
        }
    }

    @Override
    public void close() {
        inputCloseableIterator.close();
        if (currentInput != null) {
            currentInput.close();
            this.currentInput = null;
        }
    }
}
