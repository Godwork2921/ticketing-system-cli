package com.ticketing.cache;

import com.ticketing.model.Event;

import java.util.HashMap;
import java.util.Map;

public class EventCache {

    private static final Map<Long, Event> CACHE =
            new HashMap<>();

    private EventCache() {}

    public static Event get(Long id) {
        return CACHE.get(id);
    }

    public static void put(Event event) {

        if (event != null) {
            CACHE.put(event.getId(), event);
        }
    }

    public static void remove(Long id) {
        CACHE.remove(id);
    }

    public static void clear() {
        CACHE.clear();
    }

    public static boolean contains(Long id) {
        return CACHE.containsKey(id);
    }
}