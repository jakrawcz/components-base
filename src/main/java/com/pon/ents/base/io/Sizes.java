package com.pon.ents.base.io;

import static com.google.common.collect.Iterators.forArray;

import java.util.Iterator;
import java.util.stream.Stream;

import com.google.auto.value.AutoValue;
import com.pon.ents.base.io.Size.SizeUnit;
import com.pon.ents.base.io.impl.SizeRepar;
import com.pon.ents.base.text.Repar;

/**
 * A size given in bytes.
 */
public abstract class Sizes {

    /**
     * Uses a conventional {@link Repar#parse}.
     */
    public static Size parse(String string) {
        return SizeRepar.INSTANCE.parse(string);
    }

    /**
     * Creates an "empty" size.
     */
    public static Size zero() {
        return bytes(0);
    }

    /**
     * Creates a size in gigabytes.
     */
    public static Size gigabytes(long gigabytes) {
        return of(gigabytes, SizeUnit.GB);
    }

    /**
     * Creates a size in megabytes.
     */
    public static Size megabytes(long megabytes) {
        return of(megabytes, SizeUnit.MB);
    }

    /**
     * Creates a size in kilobytes.
     */
    public static Size kilobytes(long kilobytes) {
        return of(kilobytes, SizeUnit.KB);
    }

    /**
     * Creates a size in bytes.
     */
    public static Size bytes(long bytes) {
        return of(bytes, SizeUnit.B);
    }

    /**
     * Creates a size in a requested {@link SizeUnit}.
     */
    public static Size of(long count, SizeUnit sizeUnit) {
        return new AutoValue_Sizes_Immutable(sizeUnit.toBytes(count));
    }

    /**
     * Returns a sum of {@link Size}s.
     */
    public static Size sum(Iterator<Size> sizes) {
        long bytes = 0;
        while (sizes.hasNext()) {
            bytes += sizes.next().bytes();
        }
        return bytes(bytes);
    }

    /**
     * Returns a sum of {@link Size}s.
     */
    public static Size sum(Stream<Size> sizes) {
        return sum(sizes.iterator());
    }

    /**
     * Returns a sum of {@link Size}s.
     */
    public static Size sum(Iterable<Size> sizes) {
        return sum(sizes.iterator());
    }

    /**
     * Returns a sum of {@link Size}s.
     */
    public static Size sum(Size... sizes) {
        return sum(forArray(sizes));
    }

    @AutoValue
    abstract static class Immutable implements Size {

        @Override
        public String toString() {
            return SizeRepar.INSTANCE.render(this);
        }
    }
}
