package com.ticketing.util;

public class RetryUtil {

    public static <T> T execute(SupplierWithException<T> action) {

        int attempts = 0;

        while (true) {
            try {
                return action.get();

            } catch (Exception e) {

                if (++attempts >= 3)
                    throw new RuntimeException(e);

                try {
                    Thread.sleep((long) Math.pow(2, attempts) * 100L);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}