package com.pon.ents.base.io;

import com.pon.ents.base.io.impl.ByteArrayInput;
import com.pon.ents.base.io.impl.ByteInput;
import com.pon.ents.base.io.impl.ConcatenatingInput;
import com.pon.ents.base.io.impl.EmptyInput;
import com.pon.ents.base.io.impl.InputIoBufferSerie;
import com.pon.ents.base.io.impl.LimitedInput;
import com.pon.ents.base.serie.Serie;
import com.pon.ents.base.serie.Series;
import com.pon.ents.base.serie.impl.OpenedResourceClosingSerie;

public abstract class Inputs {

    private static final int DEFAULT_SERIE_BUFFER_LENGTH = 16 * 1024;

    /**
     * Returns an {@link Input} reading directly (not safe-copying) from the given byte array, from the given offset up
     * to the given limit.
     */
    public static Input fromByteArray(byte[] byteArray, int offset, int limit) {
        return new ByteArrayInput(byteArray, offset, limit);
    }

    /**
     * Performs {@link #fromByteArray(byte[], long, long)} with values unwrapped from the {@link IoBuffer}.
     */
    public static Input fromIoBuffer(IoBuffer ioBuffer) {
        return fromByteArray(ioBuffer.buffer(), ioBuffer.offset(), ioBuffer.limit());
    }

    /**
     * Performs {@link #fromByteArray(byte[], long, long)} with a limit at the end.
     */
    public static Input fromByteArray(byte[] byteArray, int offset) {
        return fromByteArray(byteArray, offset, byteArray.length - offset);
    }

    /**
     * Performs {@link #fromByteArray(byte[], long)} with an offset of 0.
     */
    public static Input fromByteArray(byte... byteArray) {
        return fromByteArray(byteArray, 0);
    }

    /**
     * A single-byte counterpart of {@link #fromByteArray}.
     */
    public static Input fromByte(byte b) {
        return new ByteInput(b);
    }

    /**
     * Returns a new empty {@link Input}.
     */
    public static Input empty() {
        return new EmptyInput();
    }

    /**
     * Returns an {@link Input} that reads from the subsequent {@link Input}s.
     * <p>
     * Since the {@link Inputs} given in the array are already opened, upon {@link Input#close() closing}, all the
     * not yet read {@link Input}s will be closed.
     */
    public static Input concat(Input... inputs) {
        return concat(new OpenedResourceClosingSerie<>(Series.of(inputs)));
    }

    /**
     * Returns an {@link Input} that reads from subsequent {@link Input}s.
     * <p>
     * Upon {@link Input#close() closing}, the {@link Serie} itself will be closed, without requesting any remaining
     * {@link Input}s.
     */
    public static Input concat(Serie<Input> inputSerie) {
        return new ConcatenatingInput(inputSerie);
    }

    /**
     * Fully buffers the given {@link Input} and returns the {@link IoBuffer}.
     */
    public static IoBuffer toIoBuffer(Input input) {
        ProducingOutput<IoBuffer> ioBufferProducingOutput = Outputs.producingIoBuffer();
        IoOperations.copy(input, ioBufferProducingOutput);
        return ioBufferProducingOutput.produce();
    }

    /**
     * Returns an {@link Input} that will end at most after the given {@code limit} bytes.
     */
    public static Input limit(Input input, long limit) {
        return new LimitedInput(input, limit);
    }

    /**
     * Performs a {@link #toIoBufferSerie(Input, int)} with a buffer of length {@value #DEFAULT_SERIE_BUFFER_LENGTH}.
     */
    public static Serie<IoBuffer> toIoBufferSerie(Input input) {
        return toIoBufferSerie(input, DEFAULT_SERIE_BUFFER_LENGTH);
    }

    /**
     * Performs a {@link #toIoBufferSerie(Input, byte[])} with a newly allocated buffer of the given size.
     */
    private static Serie<IoBuffer> toIoBufferSerie(Input input, int bufferLength) {
        return toIoBufferSerie(input, IoBuffers.allocate(bufferLength));
    }

    /**
     * Returns a {@link Serie} of {@link IoBuffer}s containing bytes of the given {@link Input}.
     * <p>
     * The previous {@link IoBuffer} needs to be fully handled before the {@link Serie#next() next one} is requested,
     * since at least the underlying {@link IoBuffer#buffer() array} will always be reused.
     */
    private static Serie<IoBuffer> toIoBufferSerie(Input input, IoBuffer buffer) {
        return new InputIoBufferSerie(input, buffer);
    }
}
