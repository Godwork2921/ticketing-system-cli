package com.ticketing.strategy;

import com.ticketing.model.Event;
import com.ticketing.model.Money;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public Money calculatePrice(Event event) {
        // Simply return the base price without modification
        return event.getBasePrice();
    }
}
