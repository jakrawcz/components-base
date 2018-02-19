package com.pon.ents.base.closeable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.pon.ents.base.closeable.impl.CompositeRuntimeCloseable;

public abstract class RuntimeCloseables {

    private static final RuntimeCloseable NOOP = () -> {};

    /**
     * Returns a no-op {@link RuntimeCloseable}.
     */
    public static RuntimeCloseable noop() {
        return NOOP;
    }

    /**
     * Returns a {@link RuntimeCloseable} that will delegate to the given {@link RuntimeCloseable}s.
     */
    public static RuntimeCloseable composite(RuntimeCloseable... runtimeCloseables) {
        return composite(Arrays.asList(runtimeCloseables));
    }

    /**
     * Returns a {@link RuntimeCloseable} that will delegate to the given {@link RuntimeCloseable}s.
     */
    public static RuntimeCloseable composite(Collection<? extends RuntimeCloseable> runtimeCloseables) {
        switch (runtimeCloseables.size()) {
            case 0: return noop();
            case 1: return Iterables.getOnlyElement(runtimeCloseables);
            default: return composite(runtimeCloseables.iterator());
        }
    }

    /**
     * Returns a {@link RuntimeCloseable} that will delegate to the given {@link RuntimeCloseable}s.
     */
    public static RuntimeCloseable composite(Iterator<? extends RuntimeCloseable> runtimeCloseables) {
        return new CompositeRuntimeCloseable(runtimeCloseables);
    }
}
