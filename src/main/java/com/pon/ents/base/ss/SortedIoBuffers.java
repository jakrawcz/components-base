package com.pon.ents.base.ss;

import java.util.Comparator;

import com.google.common.primitives.UnsignedBytes;
import com.pon.ents.base.io.IoBuffer;

/**
 * A set of utilities that all treat {@link IoBuffer}s as "sorted strings" (in terms of {@link SsTable}).
 */
public abstract class SortedIoBuffers {

    /**
     * Returns a lexicographical {@link Comparator}.
     */
    public static Comparator<IoBuffer> comparator() {
        return SortedIoBuffers::compare;
    }

    private static int compare(IoBuffer first, IoBuffer second) {
        byte[] firstBuffer = first.buffer();
        int firstOffset = first.offset();
        int firstLength = first.limit() - firstOffset;
        byte[] secondBuffer = second.buffer();
        int secondOffset = second.offset();
        int secondLength = second.limit() - secondOffset;
        int commonLength = Math.min(firstLength, secondLength);
        for (int i = 0; i < commonLength; ++i) {
            byte ofFirst = firstBuffer[firstOffset + i];
            byte ofSecond = secondBuffer[secondOffset + i];
            int comparison = UnsignedBytes.compare(ofFirst, ofSecond);
            if (comparison != 0) {
                return comparison;
            }
        }
        return Integer.compare(firstLength, secondLength);
    }
}
