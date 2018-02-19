package com.pon.ents.base.repo.impl;

import java.util.stream.Stream;

import com.pon.ents.base.blob.Blob;
import com.pon.ents.base.io.Codec;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Output;
import com.pon.ents.base.management.Manager;
import com.pon.ents.base.repo.Repository;

public class BlobManagingRepository<K, V> implements Repository<K, V> {

    private final Manager<K, Blob> blobManager;
    private final Codec<V> valueCodec;

    public BlobManagingRepository(Manager<K, Blob> blobManager, Codec<V> valueCodec) {
        this.blobManager = blobManager;
        this.valueCodec = valueCodec;
    }

    @Override
    public V get(K key) {
        try (Input input = blobManager.access(key).read()) {
            return valueCodec.decode(input);
        }
    }

    @Override
    public void set(K key, V value) {
        try (Output output = blobManager.access(key).write()) {
            valueCodec.encode(value, output);
        }
    }

    @Override
    public Stream<K> streamKeys() {
        return blobManager.streamNonEmptyObjectKeys();
    }
}
