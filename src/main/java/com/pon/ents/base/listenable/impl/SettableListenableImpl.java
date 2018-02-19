package com.pon.ents.base.listenable.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.listenable.ChangeListener;
import com.pon.ents.base.listenable.ListenerRegistration;
import com.pon.ents.base.listenable.SettableListenable;
import com.pon.ents.base.throwable.MoreThrowables;
import com.pon.ents.base.throwable.ThrowableAggregator;

public class SettableListenableImpl<T> implements SettableListenable<T> {

    private final Set<Handler> handlers;

    private T value;

    public SettableListenableImpl(T initialValue) {
        this.handlers = new HashSet<>();
        this.value = Preconditions.checkNotNull(initialValue);
    }

    @Override
    public ListenerRegistration register(ChangeListener<T> changeListener) {
        synchronized (handlers) {
            Handler handler = new Handler(changeListener);
            handlers.add(handler);
            changeListener.onChanged(null, value);
            return handler;
        }
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T newValue) {
        synchronized (handlers) {
            T previousValue = value;
            if (newValue.equals(previousValue)) {
                return;
            }
            this.value = newValue;
            try (ThrowableAggregator throwableAggregator = MoreThrowables.aggregate()) {
                for (Handler handler : handlers) {
                    @Nullable Throwable throwable = handler.notifySafely(previousValue, newValue);
                    if (throwable != null) {
                        throwableAggregator.aggregate(throwable);
                    }
                }
            }
        }
    }

    private class Handler implements ListenerRegistration {

        private final ChangeListener<T> changeListener;

        public Handler(ChangeListener<T> changeListener) {
            this.changeListener = changeListener;
        }

        @Nullable
        public Throwable notifySafely(@Nullable T previousValue, T newValue) {
            try {
                changeListener.onChanged(previousValue, newValue);
                return null;
            } catch (Throwable t) {
                return t;
            }
        }

        @Override
        public void close() {
            synchronized (handlers) {
                boolean removed = handlers.remove(this);
                Preconditions.checkState(removed, "%s already unregistered", changeListener);
            }
        }

    }
}
