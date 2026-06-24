package com.ticketing.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheSweeper {

    private final TTLCache<?, ?> cache;

    public CacheSweeper(TTLCache<?, ?> cache) {
        this.cache = cache;
    }

    public void start() {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            cache.cleanUp();
            System.out.println("[CACHE] Sweeper running...");
        }, 5, 5, TimeUnit.SECONDS);
    }
}