package com.pon.ents.base.io;

/**
 * An {@link Input}/{@link Output} two-way filter.
 * <p>
 * As a general contract, any byte stream written to an {@link Output} (via {@link #out(Output)}), when read back using
 * the {@link Input} (via {@link #in(Input)}), should turn out to contain exactly the same contents.
 * <p>
 * A common example of an {@link IoFilter} is a component that compresses bytes "out" and decompresses them back "in".
 */
public interface IoFilter {

    /**
     * Returns an {@link Output} writing to the given one, with an "out" filtering applied.
     */
    Output out(Output output);

    /**
     * Returns an {@link Input} reading from the given one, with an "in" filtering applied.
     */
    Input in(Input input);
}
