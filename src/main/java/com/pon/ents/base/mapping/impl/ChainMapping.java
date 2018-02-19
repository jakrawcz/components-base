package com.pon.ents.base.mapping.impl;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Streams;
import com.pon.ents.base.mapping.Mapping;

public class ChainMapping<K, V> implements Mapping<K, V> {

    private final Iterable<Mapping<K, V>> mappings;

    public ChainMapping(Iterable<Mapping<K, V>> mappings) {
        this.mappings = mappings;
    }

    @Override
    public Stream<Entry<K, V>> streamEntries() {
        return Streams.stream(mappings)
                .flatMap(Mapping::streamEntries)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue))
                .entrySet()
                .stream();
    }

    @Override
    @Nullable
    public V find(K key) {
        for (Mapping<K, V> mapping : mappings) {
            @Nullable V value = mapping.find(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
