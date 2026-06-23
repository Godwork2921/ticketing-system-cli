package com.ticketing.strategy;

import com.ticketing.model.Event;

public class PricingContext {

    private PricingStrategy strategy;

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(Event event) {
        if (strategy == null) {
            throw new IllegalStateException("Pricing strategy not set");
        }
        return strategy.calculatePrice(event);
    }
}