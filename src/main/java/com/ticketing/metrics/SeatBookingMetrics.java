package com.ticketing.metrics;

import java.util.concurrent.atomic.AtomicInteger;

public class SeatBookingMetrics {

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failedCount = new AtomicInteger(0);

    public static void incrementSuccess() {
        successCount.incrementAndGet();
    }

    public static void incrementFail() {
        failedCount.incrementAndGet();
    }

    public static int getSuccess() {
        return successCount.get();
    }

    public static int getFail() {
        return failedCount.get();
    }
}