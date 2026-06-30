package com.ticketing.service;

import com.ticketing.model.Event;
import com.ticketing.model.Money;
import com.ticketing.model.Seat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Currency;

public class PricingService {

    public Money calculateFinalPrice(
            Event event,
            Seat seat,
            LocalDateTime bookingTime
    ) {
        Money basePrice = event.getBasePrice();
        BigDecimal price = basePrice.amount();
        Currency currency = basePrice.currency();

        // =========================
        // VIP / REGULAR MULTIPLIER
        // =========================
        if (seat.getSection().equalsIgnoreCase("VIP")) {
            price = price.multiply(BigDecimal.valueOf(3.0));
        }

        // =========================
        // EARLY / LATE BOOKING
        // =========================
        long hoursBeforeEvent =
                Duration.between(bookingTime, event.getStartTime()).toHours();

        if (hoursBeforeEvent > 240) { // More than 10 days early
            price = price.multiply(BigDecimal.valueOf(0.80));
        } else if (hoursBeforeEvent < 24) { // Less than 24 hours
            price = price.multiply(BigDecimal.valueOf(1.30));
        }

        // =========================
        // FRONT ROW PREMIUM
        // =========================
        if (seat.getNumber() <= 5) {
            price = price.multiply(BigDecimal.valueOf(1.10));
        }

        // Round to 2 decimal places
        price = price.setScale(2, BigDecimal.ROUND_HALF_UP);

        return new Money(price, currency);
    }
}
