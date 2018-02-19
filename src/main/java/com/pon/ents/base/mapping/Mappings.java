package com.pon.ents.base.mapping;

import java.util.Map;
import java.util.function.Function;

import com.pon.ents.base.functional.Bijection;
import com.pon.ents.base.mapping.impl.ConvertingMapping;
import com.pon.ents.base.mapping.impl.MapMapping;

public abstract class Mappings {

    /**
     * Adapts a {@link Map} to a {@link Mapping}.
     */
    public static <K, V> Mapping<K, V> adapt(Map<K, V> map) {
        return new MapMapping<>(map);
    }

    /**
     * Wraps the given {@link Mapping} with a two-way key conversion and a regular value conversion.
     */
    public static <FK, TK, FV, TV> Mapping<TK, TV> converting(
            Mapping<FK, FV> mapping, Bijection<FK, TK> keyConverter, Function<FV, TV> valueConverter) {
        return new ConvertingMapping<>(mapping, keyConverter, valueConverter);
    }

    /**
     * Wraps the given {@link Mapping} with a two-way key conversion.
     */
    public static <FK, TK, V> Mapping<TK, V> keyConverting(Mapping<FK, V> mapping, Bijection<FK, TK> converter) {
        return converting(mapping, converter, Function.identity());
    }

    /**
     * Wraps the given {@link Mapping} with a value conversion.
     */
    public static <K, TV, FV> Mapping<K, FV> valueConverting(Mapping<K, TV> mapping, Function<TV, FV> converter) {
        return converting(mapping, Bijection.identity(), converter);
    }
}
