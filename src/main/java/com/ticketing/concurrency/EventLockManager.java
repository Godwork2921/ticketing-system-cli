package com.ticketing.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class EventLockManager {

    private static final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    public static ReentrantLock getLock(Long eventId) {
        return locks.computeIfAbsent(eventId, id -> new ReentrantLock(true));
    }
}