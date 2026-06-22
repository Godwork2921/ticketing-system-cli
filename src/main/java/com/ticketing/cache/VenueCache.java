package com.ticketing.cache;

import com.ticketing.model.Venue;

import java.util.HashMap;
import java.util.Map;

public class VenueCache {

    private static final Map<Long, Venue> CACHE =
            new HashMap<>();

    private VenueCache() {}

    public static Venue get(Long id) {
        return CACHE.get(id);
    }

    public static void put(Venue venue) {

        if (venue != null) {
            CACHE.put(venue.getId(), venue);
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