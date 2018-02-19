package com.pon.ents.base.io;

/**
 * An {@link Output} that {@link #produce() produces} a value (most likely the one composed from all the bytes written
 * to it).
 * <p>
 * {@link Output#close() Closing} the {@link ProducingOutput} will cancel the production (and thus only one of those
 * "terminal" methods can be called).
 */
public interface ProducingOutput<T> extends Output {

    /**
     * Returns the produced object.
     * <p>
     * It is illegal to {@link #write} any more bytes to this {@link Output} afterwards.
     */
    T produce();
}
