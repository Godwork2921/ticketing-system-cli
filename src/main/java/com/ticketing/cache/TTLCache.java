package com.ticketing.cache;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class TTLCache<K, V> {

    private final long ttlMillis;

    private final Map<K, CacheItem<V>> cache;

    public TTLCache(int maxSize, long ttlMillis) {

        this.ttlMillis = ttlMillis;

        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {

            protected boolean removeEldestEntry(Map.Entry<K, CacheItem<V>> eldest) {
                return size() > maxSize;
            }
        };
    }

    public synchronized void put(K key, V value) {
        cache.put(key, new CacheItem<>(value));
    }

    public synchronized V get(K key) {

        CacheItem<V> item = cache.get(key);

        if (item == null) return null;

        if (item.isExpired(ttlMillis)) {
            cache.remove(key);
            return null;
        }

        return item.value;
    }

    public synchronized void cleanUp() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired(ttlMillis));
    }

    private static class CacheItem<V> {

        V value;
        long createdAt = Instant.now().toEpochMilli();

        CacheItem(V value) {
            this.value = value;
        }

        boolean isExpired(long ttl) {
            return Instant.now().toEpochMilli() - createdAt > ttl;
        }
    }
}