package com.ticketing.cache;

import com.ticketing.model.Event;

import java.util.LinkedHashMap;
import java.util.Map;

public class EventCache {

    private static final int MAX_SIZE = 100;
    private static final long TTL =
            5 * 60 * 1000; // 5 minutes

    private final Map<Long, CacheEntry<Event>> cache =
            new LinkedHashMap<>(16, 0.75f, true) {

                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<Long, CacheEntry<Event>> eldest) {

                    return size() > MAX_SIZE;
                }
            };

    public synchronized void put(
            Long id,
            Event event
    ) {

        cache.put(
                id,
                new CacheEntry<>(
                        event,
                        TTL
                )
        );
    }

    public synchronized Event get(Long id) {

        CacheEntry<Event> entry =
                cache.get(id);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {

            cache.remove(id);

            return null;
        }

        return entry.getValue();
    }

    public synchronized void remove(Long id) {
        cache.remove(id);
    }

    public synchronized int size() {
        return cache.size();
    }
}