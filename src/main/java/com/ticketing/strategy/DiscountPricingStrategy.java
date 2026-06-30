package com.ticketing.strategy;

import com.ticketing.model.Event;
import com.ticketing.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountPricingStrategy implements PricingStrategy {

    private static final BigDecimal DISCOUNT_MULTIPLIER = BigDecimal.valueOf(0.7);

    @Override
    public Money calculatePrice(Event event) {
        Money basePrice = event.getBasePrice();
        BigDecimal discounted = basePrice.amount().multiply(DISCOUNT_MULTIPLIER);

        return new Money(discounted.setScale(2, RoundingMode.HALF_UP), basePrice.currency());
    }
}
