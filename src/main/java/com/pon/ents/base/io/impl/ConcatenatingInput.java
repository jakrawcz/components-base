package com.pon.ents.base.io.impl;

import javax.annotation.Nullable;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.serie.Serie;

public class ConcatenatingInput implements Input {

    private final Serie<Input> inputSerie;

    @Nullable
    private Input currentInput;

    public ConcatenatingInput(Serie<Input> inputSerie) {
        this.inputSerie = inputSerie;
        this.currentInput = null;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        while (true) {
            if (currentInput == null) {
                @Nullable Input next = inputSerie.next();
                if (next == null) {
                    return -1;
                }
                this.currentInput = next;
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
                @Nullable Input next = inputSerie.next();
                if (next == null) {
                    return -1;
                }
                this.currentInput = next;
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
        if (currentInput == null) {
            @Nullable Input next = inputSerie.next();
            if (next == null) {
                return 0;
            }
            this.currentInput = next;
        }
        return -1;
    }

    @Override
    public void close() {
        inputSerie.close();
        if (currentInput != null) {
            currentInput.close();
            this.currentInput = null;
        }
    }
}
