package com.ticketing.strategy;

import com.ticketing.model.Event;
import com.ticketing.model.Money;

public interface PricingStrategy {
    Money calculatePrice(Event event);
}
