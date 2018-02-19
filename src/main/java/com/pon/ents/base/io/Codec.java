package com.pon.ents.base.io;

import com.pon.ents.base.io.impl.DecodingOutput;

/**
 * A coder-decoder of objects of a specific type.
 * <p>
 * By default, {@link Codec}s operate on segments of byte streams (not necessarily from the beginning to the EOF), which
 * means that they must be aware of encoded object's end boundary.
 */
public interface Codec<T> {

    /**
     * Writes the given object to the {@link Output}.
     */
    void encode(T object, Output output);

    /**
     * Reads a single object from the {@link Input}.
     */
    T decode(Input input);

    /**
     * Returns an {@link Input} from which the given object can be read.
     * <p>
     * The default implementation will return a fully-buffered {@link Input}.
     */
    default Input openEncodingInput(T object) {
        ProducingOutput<IoBuffer> producingOutput = Outputs.producingIoBuffer();
        try {
            encode(object, producingOutput);
            return Inputs.fromIoBuffer(producingOutput.produce());
        } finally {
            producingOutput.close();
        }
    }

    /**
     * Returns a {@link ProducingOutput} that will decode (on {@link ProducingOutput#produce()} a single object from all
     * the bytes written to it.
     * <p>
     * It is illegal to write more bytes than required.
     */
    default ProducingOutput<T> openDecodingOutput() {
        return new DecodingOutput<>(this);
    }
}
