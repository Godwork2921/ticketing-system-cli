package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;

import java.time.LocalDateTime;

public class BasePricingStrategy implements PricingStrategy {

    @Override
    public double calculate(Event event, Seat seat, LocalDateTime time) {
        return event.getBasePrice();
    }
}