package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Money;
import com.ticketing.model.Seat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeDiscountStrategy implements PricingStrategy {

    @Override
    public Money apply(Money basePrice, Event event, Seat seat, LocalDateTime time) {
        long hours = Duration.between(time, event.getStartTime()).toHours();
        BigDecimal amount = basePrice.amount();

        if (hours > 240) { // More than 10 days early
            amount = amount.multiply(BigDecimal.valueOf(0.8));
        } else if (hours < 24) { // Less than 24 hours
            amount = amount.multiply(BigDecimal.valueOf(1.3));
        }

        return new Money(amount.setScale(2, BigDecimal.ROUND_HALF_UP), basePrice.currency());
    }
}
