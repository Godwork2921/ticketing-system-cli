package com.ticketing.concurrency;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class SeatLockManager {

    // Seat-level locks
    private static final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    // Single-flight in-flight execution tracker
    private static final ConcurrentHashMap<String, Future<?>> inFlight = new ConcurrentHashMap<>();

    private static final ExecutorService executor =
            Executors.newCachedThreadPool();

    // =========================
    // GET LOCK (FIXED TYPE)
    // =========================
    public static ReentrantLock getLock(Long eventId, Long seatId) {

        String key = key(eventId, seatId);

        return locks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    // =========================
    // SINGLE FLIGHT EXECUTION (FIXED)
    // =========================
    @SuppressWarnings("unchecked")
    public static <T> T executeOnce(String key, Callable<T> task) {

        try {

            Future<T> future = (Future<T>) inFlight.computeIfAbsent(key, k ->
                    executor.submit(task)
            );

            T result = future.get();

            inFlight.remove(key);

            return result;

        } catch (Exception e) {

            inFlight.remove(key);

            throw new RuntimeException("SingleFlight execution failed", e);
        }
    }

    // =========================
    // KEY GENERATOR
    // =========================
    public static String key(Long eventId, Long seatId) {
        return eventId + "-" + seatId;
    }
}