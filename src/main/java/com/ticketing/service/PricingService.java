package com.ticketing.service;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;

import java.time.Duration;
import java.time.LocalDateTime;

public class PricingService {

    public double calculateFinalPrice(
            Event event,
            Seat seat,
            LocalDateTime bookingTime
    ) {

        double price =
                event.getBasePrice();

        // =========================
        // VIP / REGULAR MULTIPLIER
        // =========================

        if (
                seat.getSection()
                        .equalsIgnoreCase("VIP")
        ) {

            price *= 3.0;

        } else {

            price *= 1.0;
        }

        // =========================
        // EARLY / LATE BOOKING
        // =========================

        long hoursBeforeEvent =
                Duration.between(
                                bookingTime,
                                event.getStartTime()
                        )
                        .toHours();

        // More than 10 days early
        if (hoursBeforeEvent > 240) {

            price *= 0.80;

        }

        // Less than 24 hours
        else if (hoursBeforeEvent < 24) {

            price *= 1.30;
        }

        // =========================
        // FRONT ROW PREMIUM
        // =========================

        if (
                seat.getNumber() <= 5
        ) {

            price *= 1.10;
        }

        return Math.round(
                price * 100.0
        ) / 100.0;
    }
}