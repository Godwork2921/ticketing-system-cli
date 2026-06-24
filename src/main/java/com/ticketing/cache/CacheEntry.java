package com.ticketing.cache;

public class CacheEntry<T> {

    private final T value;
    private final long expiryTime;

    public CacheEntry(
            T value,
            long ttlMillis
    ) {
        this.value = value;
        this.expiryTime =
                System.currentTimeMillis()
                        + ttlMillis;
    }

    public T getValue() {
        return value;
    }

    public boolean isExpired() {
        return System.currentTimeMillis()
                > expiryTime;
    }
}