package com.ticketing.strategy;

import com.ticketing.model.Event;
import com.ticketing.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EarlyBirdStrategy implements PricingStrategy {

    private static final BigDecimal EARLY_BIRD_MULTIPLIER = BigDecimal.valueOf(0.8);

    @Override
    public Money calculatePrice(Event event) {
        Money basePrice = event.getBasePrice();
        BigDecimal discounted = basePrice.amount().multiply(EARLY_BIRD_MULTIPLIER);

        return new Money(discounted.setScale(2, RoundingMode.HALF_UP), basePrice.currency());
    }
}
