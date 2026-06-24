package com.ticketing.metrics;

import java.util.concurrent.atomic.*;

public class Metrics {

    public static AtomicInteger totalReservations = new AtomicInteger();
    public static AtomicInteger failedReservations = new AtomicInteger();
    public static AtomicInteger holdsCreated = new AtomicInteger();
    public static AtomicInteger expiredHolds = new AtomicInteger();
}
