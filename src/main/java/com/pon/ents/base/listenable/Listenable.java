package com.pon.ents.base.listenable;

import java.util.function.Supplier;

import com.pon.ents.base.functional.Exchanger;
import com.pon.ents.base.functional.Exchangers;

/**
 * A {@link Supplier} of non-{@literal null} values supporting {@link ChangeListener} registration.
 */
public interface Listenable<T> extends Supplier<T> {

    /**
     * Registers the given {@link ChangeListener} for changes in the {@link #get() supplied} value.
     * <p>
     * It is guaranteed that the initial {@link ChangeListener#onChanged notification} will be performed in the current
     * thread.
     */
    ListenerRegistration register(ChangeListener<T> changeListener);

    @Override
    default T get() {
        Exchanger<T> expecter = Exchangers.sameThreadExpecter();
        register((irrelevant, value) -> expecter.accept(value)).close();
        return expecter.get();
    }
}
