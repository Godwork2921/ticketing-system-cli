package com.ticketing.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class SeatLockManager {

    // Seat-level locks (now reference-counted)
    private static final ConcurrentHashMap<String, CountedLock> locks = new ConcurrentHashMap<>();

    // Single-flight in-flight execution tracker
    private static final ConcurrentHashMap<String, Future<?>> inFlight = new ConcurrentHashMap<>();

    private static final ExecutorService executor =
            Executors.newCachedThreadPool();

    /**
     * A ReentrantLock paired with an atomic reference count.
     * The lock is eligible for removal from the map once refCount drops to 0.
     */
    private static class CountedLock {
        final ReentrantLock lock = new ReentrantLock();
        final AtomicInteger refCount = new AtomicInteger(1);
    }

    // =========================
    // ACQUIRE LOCK (REFERENCE-COUNTED)
    // =========================

    /**
     * Acquire a reference-counted lock for the given event/seat pair.
     * Callers MUST call {@link #releaseLock(Long, Long)} in a finally-block
     * after they are done with the lock.
     *
     * @param eventId the event id
     * @param seatId  the seat id
     * @return the underlying ReentrantLock (caller must still call lock/unlock)
     */
    public static ReentrantLock acquireLock(Long eventId, Long seatId) {
        String k = key(eventId, seatId);
        while (true) {
            CountedLock counted = locks.compute(k, (key, existing) -> {
                if (existing == null) {
                    return new CountedLock(); // starts at refCount 1
                }
                existing.refCount.incrementAndGet();
                return existing;
            });
            // Verify the entry we incremented is still the live entry.
            if (locks.get(k) == counted) {
                return counted.lock;
            }
            // Stale entry – undo increment and retry.
            counted.refCount.decrementAndGet();
        }
    }

    // =========================
    // RELEASE LOCK (REFERENCE-COUNTED)
    // =========================

    /**
     * Release the reference-counted lock for the given event/seat pair.
     * When the last reference is released the entry is removed from the map,
     * preventing memory leaks.
     *
     * @param eventId the event id
     * @param seatId  the seat id
     */
    public static void releaseLock(Long eventId, Long seatId) {
        String k = key(eventId, seatId);
        locks.compute(k, (key, existing) -> {
            if (existing == null) {
                return null;
            }
            int remaining = existing.refCount.decrementAndGet();
            return remaining <= 0 ? null : existing;
        });
    }

    // =========================
    // SINGLE FLIGHT EXECUTION (UNCHANGED)
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
    // KEY GENERATOR (UNCHANGED)
    // =========================
    public static String key(Long eventId, Long seatId) {
        return eventId + "-" + seatId;
    }

    /**
     * @deprecated Use {@link #acquireLock(Long, Long)} and {@link #releaseLock(Long, Long)} instead.
     */
    @Deprecated
    public static ReentrantLock getLock(Long eventId, Long seatId) {
        return acquireLock(eventId, seatId);
    }
}