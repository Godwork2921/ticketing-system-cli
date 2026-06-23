package com.ticketing.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheSweeper {

    public static void start(TTLCache<?, ?> cache) {

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(cache::cleanUp,
                        5, 5, TimeUnit.SECONDS);
    }
}