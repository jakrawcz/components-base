package com.pon.ents.base.blob.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Verify;
import com.pon.ents.base.blob.Blob;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Output;
import com.pon.ents.base.io.ex.RuntimeIoException;
import com.pon.ents.base.io.impl.CountingInput;
import com.pon.ents.base.io.impl.CountingOutput;

public class ResilientBlob implements Blob {

    private static final Logger LOG = LoggerFactory.getLogger(ResilientBlob.class);

    private final Blob underlying;

    public ResilientBlob(Blob underlying) {
        this.underlying = underlying;
    }

    @Override
    public Input read(long relativeOffset) {
        return new ResilientInput(relativeOffset);
    }

    @Override
    public Output write(long relativeOffset) {
        return new ResilientOutput(relativeOffset);
    }

    private class ResilientInput implements Input {

        private final long originalRelativeOffset;
        private CountingInput underlyingInput;

        public ResilientInput(long originalRelativeOffset) {
            this.originalRelativeOffset = originalRelativeOffset;
            open(0);
        }

        @Override
        public int read(byte[] buffer, int offset, int length) {
            while (true) {
                try {
                    return underlyingInput.read(buffer, offset, length);
                } catch (RuntimeIoException e) {
                    handleResiliently(e);
                }
            }
        }

        @Override
        public long remaining() {
            return underlyingInput.remaining();
        }

        @Override
        public void close() {
            underlyingInput.close();
        }

        @Override
        public int read() {
            while (true) {
                try {
                    return underlyingInput.read();
                } catch (RuntimeIoException e) {
                    handleResiliently(e);
                }
            }
        }

        private void handleResiliently(RuntimeIoException e) {
            long readCount = underlyingInput.readCount();
            LOG.warn("an I/O error occurred while reading " + underlying + " (originally opened at "
                    + originalRelativeOffset + ") after reading " + readCount + " bytes: " + e
                    + "; re-opening it");
            open(readCount);
        }

        private void open(long readCount) {
            this.underlyingInput = new CountingInput(underlying.read(currentRelativeOffset(readCount)), readCount);
        }

        private long currentRelativeOffset(long readCount) {
            if (originalRelativeOffset < 0) {
                long originalBackwardOffset = -originalRelativeOffset - 1;
                Verify.verify(originalBackwardOffset >= readCount,
                        "input was opened with relative offset %s, which means for last %s bytes, but read %s bytes",
                        originalRelativeOffset, originalBackwardOffset, readCount);
                long backwardOffset = originalBackwardOffset - readCount;
                return -backwardOffset - 1;
            } else {
                return originalRelativeOffset + readCount;
            }
        }
    }

    private class ResilientOutput implements Output {

        private final long originalRelativeOffset;
        private CountingOutput underlyingOutput;

        public ResilientOutput(long originalRelativeOffset) {
            this.originalRelativeOffset = originalRelativeOffset;
            open(0);
        }

        @Override
        public void write(byte[] buffer, int offset, int length) {
            while (true) {
                try {
                    underlyingOutput.write(buffer, offset, length);
                } catch (RuntimeIoException e) {
                    handleResiliently(e);
                }
            }
        }

        @Override
        public void close() {
            underlyingOutput.close();
        }

        @Override
        public void write(int b) {
            while (true) {
                try {
                    underlyingOutput.write(b);
                } catch (RuntimeIoException e) {
                    handleResiliently(e);
                }
            }
        }

        private void handleResiliently(RuntimeIoException e) {
            long writtenCount = underlyingOutput.writtenCount();
            LOG.warn("an I/O error occurred while writing " + underlying + " (originally opened at "
                    + originalRelativeOffset + ") after writing " + writtenCount + " bytes: " + e
                    + "; re-opening it");
            open(writtenCount);
        }

        private void open(long writtenCount) {
            this.underlyingOutput = new CountingOutput(
                    underlying.write(currentRelativeOffset(writtenCount)), writtenCount);
        }

        private long currentRelativeOffset(long readCount) {
            if (originalRelativeOffset < 0) {
                long originalBackwardOffset = -originalRelativeOffset - 1;
                long backwardOffset = Math.max(0, originalBackwardOffset - readCount);
                return -backwardOffset - 1;
            } else {
                return originalRelativeOffset + readCount;
            }
        }
    }
}
