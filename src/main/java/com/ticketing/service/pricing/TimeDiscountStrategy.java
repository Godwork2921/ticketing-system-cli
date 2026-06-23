package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeDiscountStrategy implements PricingStrategy {

    @Override
    public double calculate(Event event, Seat seat, LocalDateTime time) {

        long hours = Duration.between(time, event.getStartTime()).toHours();

        double price = event.getBasePrice();

        if (hours > 240) {
            price *= 0.8;
        } else if (hours < 24) {
            price *= 1.3;
        }

        return price;
    }
}