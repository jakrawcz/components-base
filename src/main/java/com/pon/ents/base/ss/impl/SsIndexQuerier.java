package com.pon.ents.base.ss.impl;

import java.util.Iterator;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.Input;

public interface SsIndexQuerier {

    long OFFSET_TOO_LOW = -2;
    long OFFSET_TOO_HIGH = -3;

    SsIndexQuerier EMPTY = prefix -> OFFSET_TOO_LOW;

    long offsetOf(Input prefix);

    default void offsetsOf(Input[] inputs, long[] offsets) {
        Preconditions.checkArgument(inputs.length == offsets.length, "non-matching arrays");
        for (int i = 0; i < offsets.length; ++i) {
            offsets[i] = offsetOf(inputs[i]);
        }
    }

    default void offsetsOf(Iterator<Input> inputs, long[] offsets) {
        for (int i = 0; i < offsets.length; ++i) {
            offsets[i] = offsetOf(inputs.next());
        }
        Preconditions.checkArgument(!inputs.hasNext(), "offsets array too small");
    }

    default long[] offsetsOf(Input... inputs) {
        long[] offsets = new long[inputs.length];
        offsetsOf(inputs, offsets);
        return offsets;
    }
}
