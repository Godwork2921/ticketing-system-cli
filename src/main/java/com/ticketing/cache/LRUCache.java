package com.ticketing.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.*;

public class LRUCache<K,V> {

    private final int capacity;

    private final Map<K,V> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;

        this.cache = Collections.synchronizedMap(
                new LinkedHashMap<>(16, 0.75f, true) {

                    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                        return size() > capacity;
                    }
                }
        );
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }
}