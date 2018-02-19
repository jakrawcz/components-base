package com.pon.ents.base.ss;

public interface SsTableAccessor {

    /**
     * TODO: document
     */
    SsTable get();

    void set(SsTable ssTable);

    default void add(SsTable ssTable) {
        set(SsTables.add(get(), ssTable));
    }
}
