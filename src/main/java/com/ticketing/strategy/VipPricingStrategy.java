package com.ticketing.strategy;

import com.ticketing.model.Event;

public class VipPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Event event) {
        return event.getBasePrice() * 2;
    }
}