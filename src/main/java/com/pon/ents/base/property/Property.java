package com.pon.ents.base.property;

import com.pon.ents.base.listenable.Listenable;

/**
 * A dynamic property of a specific name (please see the {@link PropertyProvider}).
 * <p>
 * A property is defined (in context of the requested data type) if the underlying source of data contains a valid,
 * parseable entry associated (in some implementation-specific way) with the aforementioned name. E.g. a property of
 * name "foo" and value "var" is defined in context of {@link String}, and at the same time is not defined in context of
 * {@literal int}.
 */
public interface Property {

    /**
     * Returns a {@link Listenable} value of this {@link Property} (or of the given {@code defaultValue}, whenever
     * undefined).
     */
    Listenable<String> definedOr(String defaultValue);

    /**
     * Returns a {@link Listenable} value of this {@link Property} (or of the given {@code defaultValue}, whenever
     * undefined).
     */
    Listenable<Integer> definedOr(int defaultValue);

    /**
     * Returns a {@link Listenable} value of this {@link Property} (or of the given {@code defaultValue}, whenever
     * undefined).
     */
    Listenable<Long> definedOr(long defaultValue);

    /**
     * Returns a {@link Listenable} value of this {@link Property} (or of the given {@code defaultValue}, whenever
     * undefined).
     */
    Listenable<Double> definedOr(double defaultValue);

    /**
     * Returns a {@link Listenable} value of this {@link Property} (or of the given {@code defaultValue}, whenever
     * undefined).
     */
    Listenable<Boolean> definedOr(boolean defaultValue);
}
