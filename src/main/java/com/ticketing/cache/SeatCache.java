package com.ticketing.cache;

import com.ticketing.model.Seat;

import java.util.HashMap;
import java.util.Map;

public class SeatCache {

    // KEY = seatId
    private static final Map<Long, Seat> CACHE =
            new HashMap<>();

    private SeatCache() {}

    public static Seat get(Long seatId) {
        return CACHE.get(seatId);
    }

    public static void put(Seat seat) {

        if (seat != null) {

            CACHE.put(seat.getId(), seat);

            System.out.println(
                    "[CACHE STORE] Seat " + seat.getId()
            );
        }
    }

    public static void remove(Long seatId) {
        CACHE.remove(seatId);
    }

    public static void clear() {
        CACHE.clear();
    }

    public static boolean contains(Long seatId) {
        return CACHE.containsKey(seatId);
    }
}