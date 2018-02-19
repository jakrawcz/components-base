package com.pon.ents.base.management.impl;

import java.util.function.Function;
import java.util.stream.Stream;

import com.pon.ents.base.functional.Bijection;
import com.pon.ents.base.management.Manager;

public class ConvertingManager<FK, FT, TK, TT> implements Manager<TK, TT> {

    private final Manager<FK, FT> underlying;
    private final Bijection<FK, TK> keyConverter;
    private final Function<FT, TT> objectConverter;

    public ConvertingManager(
            Manager<FK, FT> underlying,
            Bijection<FK, TK> keyConverter,
            Function<FT, TT> objectConverter) {
        this.underlying = underlying;
        this.keyConverter = keyConverter;
        this.objectConverter = objectConverter;
    }

    @Override
    public TT access(TK key) {
        return objectConverter.apply(underlying.access(keyConverter.backward(key)));
    }

    @Override
    public Stream<TK> streamNonEmptyObjectKeys() {
        return underlying.streamNonEmptyObjectKeys().map(keyConverter::forward);
    }
}
