package com.ticketing.metrics;

import java.util.concurrent.atomic.AtomicInteger;

public class SystemMetrics {

    public static final AtomicInteger SUCCESS_BOOKINGS =
            new AtomicInteger();

    public static final AtomicInteger FAILED_BOOKINGS =
            new AtomicInteger();
}