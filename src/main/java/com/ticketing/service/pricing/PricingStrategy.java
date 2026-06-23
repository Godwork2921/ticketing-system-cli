package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;

import java.time.LocalDateTime;

public interface PricingStrategy {
    double calculate(Event event, Seat seat, LocalDateTime time);
}