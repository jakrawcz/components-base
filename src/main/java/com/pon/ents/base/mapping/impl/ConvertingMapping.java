package com.pon.ents.base.mapping.impl;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.pon.ents.base.functional.Bijection;
import com.pon.ents.base.mapping.Mapping;

public class ConvertingMapping<FK, FV, TK, TV> implements Mapping<TK, TV> {

    private final Mapping<FK, FV> underlying;
    private final Bijection<FK, TK> keyConverter;
    private final Function<FV, TV> valueConverter;

    public ConvertingMapping(
            Mapping<FK, FV> underlying,
            Bijection<FK, TK> keyConverter,
            Function<FV, TV> valueConverter) {
        this.underlying = underlying;
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public Stream<Entry<TK, TV>> streamEntries() {
        return underlying.streamEntries().map(this::toEntry);
    }

    @Override
    @Nullable
    public TV find(TK key) {
        @Nullable FV originalValue = underlying.find(keyConverter.backward(key));
        return originalValue == null ? null : valueConverter.apply(originalValue);
    }

    private Entry<TK, TV> toEntry(Entry<FK, FV> entry) {
        return Maps.immutableEntry(keyConverter.forward(entry.getKey()), valueConverter.apply(entry.getValue()));
    }
}
