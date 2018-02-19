package com.pon.ents.base.io;

import com.google.common.base.Verify;

public abstract class IoOperations {

    private static final int COPY_BUFFER_LENGTH = 4096;
    private static final long BUFFERED_COPY_THRESHOLD = 9;

    /**
     * The most classic I/O operation - fully reads the given {@link Input} and puts its bytes in the given
     * {@link Output} (does not close it).
     */
    public static void copy(Input input, Output output) {
        long remaining = input.remaining();
        if (remaining == -1 || remaining >= BUFFERED_COPY_THRESHOLD) {
            int bufferLength = (int) (remaining == -1 ? COPY_BUFFER_LENGTH : Math.min(remaining, COPY_BUFFER_LENGTH));
            byte[] buffer = new byte[bufferLength];
            while (true) {
                int read = input.read(buffer);
                if (read == -1) {
                    break;
                }
                output.write(buffer, 0, read);
            }
        } else {
            for (int i = 0; i < remaining; ++i) {
                output.write(input.read());
            }
            int nextRead = input.read();
            Verify.verify(nextRead == -1, "input %s declared %s remaining bytes, but were able to read one more: %s",
                    input, remaining, nextRead);
        }
    }
}
