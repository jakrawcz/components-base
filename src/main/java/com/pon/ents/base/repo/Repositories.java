package com.pon.ents.base.repo;

import com.pon.ents.base.blob.Blob;
import com.pon.ents.base.io.Codec;
import com.pon.ents.base.management.Manager;
import com.pon.ents.base.repo.impl.BlobManagingRepository;

public abstract class Repositories {

    /**
     * Returns a {@link Repository} that will use the given {@link Manager} of {@link Blob}s as a low-level storage,
     * with the given {@link Codec} for values.
     */
    public static <K, V> Repository<K, V> onBlobManager(Manager<K, Blob> blobManager, Codec<V> valueCodec) {
        return new BlobManagingRepository<>(blobManager, valueCodec);
    }
}
