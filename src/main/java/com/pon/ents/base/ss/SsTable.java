package com.pon.ents.base.ss;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.serie.Serie;
import com.pon.ents.base.ss.impl.SubSsTable;

/**
 * A sorted strings table.
 * <p>
 * The "string" here is represented by an {@link Input}, and is "sorted" lexicographically in terms of its subsequent
 * <b>unsigned</b> bytes. Duplicates are allowed.
 * <p>
 * All the {@link Input}s passed to the methods of {@link SsTable} will be fully read or {@link Input#close() closed}
 * before return.
 */
public interface SsTable {

    /**
     * Returns a {@link Serie} of sorted strings equal to or greater than the given {@code from} and lesser than the
     * given {@code to}.
     * <p>
     * The {@link Serie#next provided} {@link Input}s must be consumed sequentially - upon advancing, the previously
     * returned {@link Input} will no longer be valid (and reading it will result in unspecified behavior). The consumer
     * is not required to {@link Input#close()} any of these {@link Input}s (but closing each of them once will of
     * course be legal). Closing the {@link Serie} itself will also invalidate the last returned {@link Input}.
     */
    Serie<Input> get(Input from, Input to);

    /**
     * Returns an {@link #iterator(Input, Input)} that will go to the end.
     */
    default Serie<Input> get(Input from) {
        return get(from, SortedInputs.maximum());
    }

    /**
     * Returns an {#iterator(Input)} that will start from the beginning.
     */
    default Serie<Input> get() {
        return get(SortedInputs.minimum());
    }

    /**
     * Returns an {@link SsTable} limited to the given {@code from} and {@code to}.
     */
    default SsTable subTable(Input from, Input to) {
        return new SubSsTable(this, from, to);
    }
}
