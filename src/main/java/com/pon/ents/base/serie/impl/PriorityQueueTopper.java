package com.pon.ents.base.serie.impl;

import java.util.Comparator;
import java.util.PriorityQueue;

import javax.annotation.Nullable;


public class PriorityQueueTopper<T> implements Topper<T> {

    private final PriorityQueue<T> priorityQueue;

    public PriorityQueueTopper(Comparator<? super T> comparator) {
        this.priorityQueue = new PriorityQueue<>(comparator);
    }

    @Override
    public void add(T object) {
        priorityQueue.add(object);
    }

    @Override
    @Nullable
    public T removeTop() {
        return priorityQueue.poll();
    }
}
