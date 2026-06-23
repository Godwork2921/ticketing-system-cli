package com.ticketing.cache;

import com.ticketing.model.Event;

import java.util.HashMap;
import java.util.Map;

public class EventCache {

    // KEY = eventId
    private static final Map<Long, Event> CACHE =
            new HashMap<>();

    private EventCache() {}

    public static Event get(Long eventId) {
        return CACHE.get(eventId);
    }

    public static void put(Event event) {

        if (event != null) {

            CACHE.put(event.getId(), event);

            System.out.println(
                    "[CACHE STORE] Event " + event.getId()
            );
        }
    }

    public static void remove(Long eventId) {
        CACHE.remove(eventId);
    }

    public static void clear() {
        CACHE.clear();
    }

    public static boolean contains(Long eventId) {
        return CACHE.containsKey(eventId);
    }
}