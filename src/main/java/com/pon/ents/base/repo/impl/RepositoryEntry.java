package com.pon.ents.base.repo.impl;

import java.util.Map.Entry;

import com.pon.ents.base.repo.Repository;

public class RepositoryEntry<K, V> implements Entry<K, V> {

    private final Repository<K, V> repository;
    private final K key;

    public RepositoryEntry(Repository<K, V> repository, K key) {
        this.repository = repository;
        this.key = key;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return repository.get(key);
    }

    @Override
    public V setValue(V value) {
        V previousValue = getValue();
        repository.set(key, value);
        return previousValue;
    }
}
