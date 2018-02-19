package com.pon.ents.base.closeable;

import java.util.Iterator;

/**
 * A {@link RuntimeCloseable} {@link Iterator}.
 */
public interface CloseableIterator<T> extends Iterator<T>, RuntimeCloseable {
}
