package com.pon.ents.base.io;

/**
 * A size given in bytes.
 */
public interface Size extends Comparable<Size> {

    /**
     * A classic order of magnitude of cardinality in computing.
     */
    long ORDER_OF_MAGNITUDE = 1L << 10;

    /**
     * A unit of size.
     */
    enum SizeUnit {

        B,
        KB(ORDER_OF_MAGNITUDE, B),
        MB(ORDER_OF_MAGNITUDE, KB),
        GB(ORDER_OF_MAGNITUDE, MB),
        TB(ORDER_OF_MAGNITUDE, GB),
        PB(ORDER_OF_MAGNITUDE, TB);

        private final long multiplier;

        private SizeUnit() {
            this.multiplier = 1L;
        }

        private SizeUnit(long multiplier, SizeUnit sizeUnit) {
            this.multiplier = multiplier * sizeUnit.multiplier;
        }

        /**
         * Returns a number of bytes in the {@code count} units.
         */
        public long toBytes(long count) {
            return count * multiplier;
        }
    }

    /**
     * The exact size in bytes.
     */
    long bytes();

    /**
     * True if less than other {@link Size}.
     */
    default boolean isLessThan(Size other) {
        return bytes() < other.bytes();
    }

    /**
     * True if greater than other {@link Size}.
     */
    default boolean isGreaterThan(Size size) {
        return bytes() > size.bytes();
    }

    @Override
    default int compareTo(Size o) {
        return Long.compare(bytes(), o.bytes());
    }
}