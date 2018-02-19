package com.pon.ents.base.mapping.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.pon.ents.base.mapping.Mapping;

public class MapMapping<K, V> implements Mapping<K, V> {

    private final Map<K, V> map;

    public MapMapping(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public Stream<Entry<K, V>> streamEntries() {
        return map.entrySet().stream();
    }

    @Override
    @Nullable
    public V find(K key) {
        return map.get(key);
    }
}
