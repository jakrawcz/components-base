package com.pon.ents.base.queue;

import com.google.common.base.Preconditions;
import com.pon.ents.base.listenable.Listenable;
import com.pon.ents.base.queue.impl.ArrayCloseableQueue;
import com.pon.ents.base.queue.impl.DynamicCapacityCloseableQueue;
import com.pon.ents.base.queue.impl.SynchronousCloseableQueue;

public abstract class CloseableQueues {

    /**
     * TODO: document
     */
    public static <T> CloseableQueue<T> fixedCapacity(int capacity) {
        Preconditions.checkArgument(capacity >= 0, "illegal capacity %s", capacity);
        if (capacity == 0) {
            return new SynchronousCloseableQueue<>();
        } else {
            return new ArrayCloseableQueue<>(capacity);
        }
    }

    /**
     * TODO: document
     */
    public static <T> CloseableQueue<T> dynamicCapacity(Listenable<Integer> capacity) {
        return new DynamicCapacityCloseableQueue<>(CloseableQueues::fixedCapacity, capacity);
    }
}
