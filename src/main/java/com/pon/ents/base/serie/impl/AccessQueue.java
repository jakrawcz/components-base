package com.pon.ents.base.serie.impl;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

public class AccessQueue<T> {

    private static final int MINIMUM_CAPACITY = 16;

    private T[] elements;
    private int head;
    private int tail;
    private long removedCount;

    public AccessQueue() {
        this.elements = allocate(MINIMUM_CAPACITY);
        this.head = 0;
        this.tail = 0;
        this.removedCount = 0;
    }

    public void add(T element) {
        Preconditions.checkNotNull(element);
        elements[tail] = element;
        this.tail = (tail + 1) & (elements.length - 1);
        if (tail == head) {
            grow();
        }
    }

    public T remove() {
        @Nullable T result = elements[head];
        if (result == null) {
            throw new NoSuchElementException();
        }
        elements[head] = null;
        int capacity = elements.length;
        this.head = (head + 1) & (capacity - 1);
        if (capacity >= (MINIMUM_CAPACITY << 1) && size() == (capacity >> 2)) {
            shrink();
        }
        ++this.removedCount;
        return result;
    }

    @Nullable
    public T peek() {
        return elements[head];
    }

    @Nullable
    public T access(long addedIndex) {
        int storedIndex = Ints.checkedCast(addedIndex - removedCount);
        int capacity = elements.length;
        int size = (tail - head) & (capacity - 1);
        if (storedIndex < 0 || storedIndex >= size) {
            return null;
        }
        int index = (head + storedIndex) & (capacity - 1);
        return elements[index];
    }

    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    public long addedCount() {
        return removedCount + size();
    }

    public long removedCount() {
        return removedCount;
    }

    @Override
    public String toString() {
        int size = size();
        return Iterators.toString(IntStream.range(0, size)
                .mapToObj(i -> Maps.immutableEntry(
                        removedCount + size - i - 1,
                        elements[(tail - i - 1) & (elements.length - 1)]))
                .iterator());
    }

    private void grow() {
        int capacity = elements.length;
        int newCapacity = capacity << 1;
        Preconditions.checkState(newCapacity >= 0, "cannot grow beyond capacity %s", capacity);
        int rightLength = capacity - head;
        T[] newElements = allocate(newCapacity);
        System.arraycopy(elements, head, newElements, 0, rightLength);
        System.arraycopy(elements, 0, newElements, rightLength, head);
        this.elements = newElements;
        this.head = 0;
        this.tail = capacity;
    }

    private void shrink() {
        int capacity = elements.length;
        int newCapacity = capacity >> 1;
        T[] newElements = allocate(newCapacity);
        if (tail >= head) {
            System.arraycopy(elements, head, newElements, 0, tail - head);
        } else {
            int rightLength = capacity - head;
            System.arraycopy(elements, head, newElements, 0, rightLength);
            System.arraycopy(elements, 0, newElements, rightLength, tail);
        }
        this.elements = newElements;
        this.head = 0;
        this.tail = capacity >> 2;
    }

    @SuppressWarnings("unchecked")
    private T[] allocate(int newCapacity) {
        return (T[]) new Object[newCapacity];
    }
}
