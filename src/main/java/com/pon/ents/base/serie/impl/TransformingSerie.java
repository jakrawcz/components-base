package com.pon.ents.base.serie.impl;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.serie.Serie;

public class TransformingSerie<T> implements Serie<T> {

    private final Serie<Object> underlying;
    private final Function<Object, ? extends T> function;

    @SuppressWarnings("unchecked")
    public <F> TransformingSerie(Serie<F> underlying, Function<? super F, ? extends T> function) {
        this.underlying = (Serie<Object>) underlying;
        this.function = new NullTerminatorAwareFunction<>((Function<Object, ? extends T>) function);
    }

    @Override
    public void close() {
        underlying.close();
    }

    @Override
    @Nullable
    public T next() {
        return function.apply(underlying.next());
    }

    @Override
    public long remaining() {
        return underlying.remaining();
    }

    private static class NullTerminatorAwareFunction<F, T> implements Function<F, T> {

        private final Function<F, T> underlying;

        public NullTerminatorAwareFunction(Function<F, T> underlying) {
            this.underlying = underlying;
        }

        @Override
        @Nullable
        public T apply(@Nullable F element) {
            if (element == null) {
                return null;
            }
            T transformed = underlying.apply(element);
            Preconditions.checkNotNull(transformed, "underlying function contract violation");
            return transformed;
        }
    }
}
