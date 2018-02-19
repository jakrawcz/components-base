package com.pon.ents.base.blob.impl;

import com.google.common.base.Preconditions;

public abstract class RelativeOffsets {

    public static long toOffset(long relativeOffset, long length) {
        long offset = relativeOffset < 0 ? length + 1 + relativeOffset : relativeOffset;
        Preconditions.checkArgument(offset >= 0 && offset <= length,
                "illegal relative offset %s (for length %s)", relativeOffset, length);
        return offset;
    }
}
