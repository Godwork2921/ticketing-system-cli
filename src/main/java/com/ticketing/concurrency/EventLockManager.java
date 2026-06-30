package com.ticketing.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Production-style lock manager using AutoCloseable guard pattern.
 *
 * ✔ No memory leak
 * ✔ No manual release required
 * ✔ Safe usage via try-with-resources
 */
public class EventLockManager {

    private static final ConcurrentHashMap<Long, LockEntry> locks = new ConcurrentHashMap<>();

    // =====================================================
    // INTERNAL LOCK HOLDER
    // =====================================================
    private static class LockEntry {
        final ReentrantLock lock = new ReentrantLock(true);
        final AtomicInteger refCount = new AtomicInteger(0);
    }

    // =====================================================
    // LOCK GUARD (AUTO-CLOSEABLE)
    // =====================================================
    public static class LockGuard implements AutoCloseable {

        private final Long eventId;
        private final ReentrantLock lock;
        private boolean released = false;

        private LockGuard(Long eventId, ReentrantLock lock) {
            this.eventId = eventId;
            this.lock = lock;
        }

        public ReentrantLock getLock() {
            return lock;
        }

        @Override
        public void close() {
            if (released) return;
            released = true;

            locks.computeIfPresent(eventId, (id, entry) -> {
                int remaining = entry.refCount.decrementAndGet();
                return remaining <= 0 ? null : entry;
            });

            lock.unlock();
        }
    }

    // =====================================================
    // MAIN API (SAFE ENTRY POINT)
    // =====================================================
    public static LockGuard lock(Long eventId) {

        LockEntry entry = locks.computeIfAbsent(eventId, id -> new LockEntry());

        entry.refCount.incrementAndGet();

        ReentrantLock lock = entry.lock;
        lock.lock();

        return new LockGuard(eventId, lock);
    }

    // =====================================================
    // BACKWARD COMPATIBILITY (OPTIONAL)
    // =====================================================
    @Deprecated
    public static ReentrantLock getLock(Long eventId) {
        return lock(eventId).getLock();
    }
}