package com.pon.ents.base.ss;

import java.util.Comparator;

import com.google.common.primitives.UnsignedBytes;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.Outputs;
import com.pon.ents.base.io.ProducingOutput;
import com.pon.ents.base.ss.impl.MaximumInput;

/**
 * A set of utilities that all treat {@link Input}s as "sorted strings" (in terms of {@link SsTable}).
 */
public abstract class SortedInputs {

    /**
     * Returns a value conforming to the {@link Comparator} contract, but at the same time guarantees that both of the
     * given {@link Input}s will be either fully read, or closed (as reading is required for comparison).
     * <p>
     * That makes this method not suitable for any usual {@link Comparator} usage (e.g. sorting, picking maximum, ...).
     */
    public static int compare(Input first, Input second) {
        while (true) {
            int ofFirst = first.read();
            int ofSecond = second.read();
            int comparison = ofFirst - ofSecond;
            if (comparison != 0) {
                first.close();
                second.close();
                return comparison;
            } else if (ofFirst == -1) {
                return 0;
            }
        }
    }

    /**
     * Returns a greater one of the two {@link Input}s.
     * <p>
     * Please note that it is not possible to compare {@link Input}s without reading them. Thus, both of the passed
     * {@link Input}s will be (at least partially) consumed and either {@link Input#close() closed} or re-used for the
     * result. The returned {@link Input} will be functionally "a newly opened" one.
     */
    public static Input greaterOf(Input first, Input second) {
        ProducingOutput<IoBuffer> ioBufferProducingOutput = Outputs.producingIoBuffer();
        while (true) {
            int ofFirst = first.read();
            int ofSecond = second.read();
            int comparison = ofFirst - ofSecond;
            if (comparison > 0) {
                ioBufferProducingOutput.write(ofFirst);
                second.close();
                return Inputs.concat(Inputs.fromIoBuffer(ioBufferProducingOutput.produce()), first);
            } else if (comparison < 0) {
                ioBufferProducingOutput.write(ofSecond);
                first.close();
                return Inputs.concat(Inputs.fromIoBuffer(ioBufferProducingOutput.produce()), second);
            } else if (ofFirst == -1) {
                return Inputs.fromIoBuffer(ioBufferProducingOutput.produce());
            }
            ioBufferProducingOutput.write(ofFirst);
        }
    }

    /**
     * Returns a lesser one of the two {@link Input}s.
     * <p>
     * Please see the {@link #greaterOf} contract for considerations regarding passed and returned {@link Input}s.
     */
    public static Input lesserOf(Input first, Input second) {
        ProducingOutput<IoBuffer> ioBufferProducingOutput = Outputs.producingIoBuffer();
        while (true) {
            int ofFirst = first.read();
            int ofSecond = second.read();
            int comparison = ofFirst - ofSecond;
            if (comparison > 0) {
                first.close();
                if (ofSecond == -1) {
                    return Inputs.fromIoBuffer(ioBufferProducingOutput.produce());
                } else {
                    ioBufferProducingOutput.write(ofSecond);
                    return Inputs.concat(Inputs.fromIoBuffer(ioBufferProducingOutput.produce()), second);
                }
            } else if (comparison < 0) {
                second.close();
                if (ofFirst == -1) {
                    return Inputs.fromIoBuffer(ioBufferProducingOutput.produce());
                } else {
                    ioBufferProducingOutput.write(ofFirst);
                    return Inputs.concat(Inputs.fromIoBuffer(ioBufferProducingOutput.produce()), first);
                }
            } else if (ofFirst == -1) {
                return Inputs.fromIoBuffer(ioBufferProducingOutput.produce());
            }
            ioBufferProducingOutput.write(ofFirst);
        }
    }

    /**
     * Returns an {@link Input} that is lesser than all other {@link Input}s.
     * <p>
     * Lexicographically, this happens to be an {@link Inputs#empty()}.
     */
    public static Input minimum() {
        return Inputs.empty();
    }

    /**
     * Returns an {@link Input} that is greater than all other {@link Input}s.
     * <p>
     * This happens to be an infinite series of {@link UnsignedBytes#MAX_VALUE}.
     */
    public static Input maximum() {
        return new MaximumInput();
    }
}
