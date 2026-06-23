package com.ticketing.cache;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class TTLCache<K, V> {

    private static class Entry<V> {
        V value;
        long expiry;

        Entry(V value, long ttlMillis) {
            this.value = value;
            this.expiry = System.currentTimeMillis() + ttlMillis;
        }
    }

    private final ConcurrentHashMap<K, Entry<V>> map = new ConcurrentHashMap<>();

    public void put(K key, V value, long ttlMillis) {
        map.put(key, new Entry<>(value, ttlMillis));
    }

    public V get(K key) {
        Entry<V> entry = map.get(key);

        if (entry == null) return null;

        if (System.currentTimeMillis() > entry.expiry) {
            map.remove(key);
            return null;
        }

        return entry.value;
    }

    public void cleanUp() {
        map.entrySet().removeIf(e ->
                System.currentTimeMillis() > e.getValue().expiry
        );
    }
}