package com.ticketing.cache;

import com.ticketing.model.Seat;

import java.util.HashMap;
import java.util.Map;

public class SeatCache {

    private static final Map<Long, Seat> CACHE =
            new HashMap<>();

    private SeatCache() {}

    public static Seat get(Long id) {
        return CACHE.get(id);
    }

    public static void put(Seat seat) {

        if (seat != null) {
            CACHE.put(seat.getId(), seat);
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