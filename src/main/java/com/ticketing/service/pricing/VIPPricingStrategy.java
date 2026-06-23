package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;

import java.time.LocalDateTime;

public class VIPPricingStrategy implements PricingStrategy {

    @Override
    public double calculate(Event event, Seat seat, LocalDateTime time) {

        if ("VIP".equalsIgnoreCase(seat.getSection())) {
            return event.getBasePrice() * 3;
        }

        return event.getBasePrice();
    }
}