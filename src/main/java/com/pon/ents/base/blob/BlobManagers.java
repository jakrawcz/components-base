package com.pon.ents.base.blob;

import java.util.Map;

import com.pon.ents.base.blob.impl.IoFilterApplicator;
import com.pon.ents.base.blob.impl.MemoryBlobManager;
import com.pon.ents.base.blob.impl.ResilientBlob;
import com.pon.ents.base.functional.MorePredicates;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.IoFilter;
import com.pon.ents.base.io.Output;
import com.pon.ents.base.io.ex.RuntimeIoException;
import com.pon.ents.base.management.Manager;
import com.pon.ents.base.management.Managers;

public abstract class BlobManagers {

    /**
     * Returns a {@link Manager} of {@link Blob}s backed by the given {@link Map}.
     */
    public static <K> Manager<K, Blob> inMemory(Map<K, IoBuffer> map) {
        return new MemoryBlobManager<>(map);
    }

    /**
     * Returns a {@link Manager} that will pass all the accessed {@link Blob}s (or rather, their opened {@link Input}s
     * and {@link Output}s) through the given {@link IoFilter}.
     */
    public static <K> Manager<K, Blob> ioFiltering(Manager<K, Blob> underlying, IoFilter ioFilter) {
        return Managers.objectConverting(underlying, new IoFilterApplicator(ioFilter));
    }

    /**
     * Returns a {@link Manager} that will automatically re-open {@link Input}s and {@link Output}s of the accessed
     * {@link Blob}s (at the right offset) after they cause an I/O error (i.e. throw {@link RuntimeIoException}),
     * both during opening, or during read/write.
     */
    public static <K> Manager<K, Blob> makeResilient(Manager<K, Blob> underlying) {
        return Managers.retrying(
                Managers.objectConverting(underlying, ResilientBlob::new),
                MorePredicates.isInstanceOf(RuntimeIoException.class));
    }
}
