package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;
import com.ticketing.model.Money;

import java.time.LocalDateTime;

public interface PricingStrategy {
    Money apply(Money price, Event event, Seat seat, LocalDateTime time);
}
