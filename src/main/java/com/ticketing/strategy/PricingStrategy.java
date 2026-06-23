package com.ticketing.strategy;

import com.ticketing.model.Event;

public interface PricingStrategy {
    double calculatePrice(Event event);
}