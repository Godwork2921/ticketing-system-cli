package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;
import com.ticketing.model.Money;

import java.time.LocalDateTime;

public class BasePricingStrategy implements PricingStrategy {

    @Override
    public Money apply(Money price, Event event, Seat seat, LocalDateTime time) {
        // No modification, just return the base price
        return price;
    }
}
