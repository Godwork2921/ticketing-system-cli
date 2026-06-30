package com.ticketing.strategy;

import com.ticketing.model.Event;
import com.ticketing.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VipPricingStrategy implements PricingStrategy {

    private static final BigDecimal VIP_MULTIPLIER = BigDecimal.valueOf(2.0);

    @Override
    public Money calculatePrice(Event event) {
        Money basePrice = event.getBasePrice();
        BigDecimal newAmount = basePrice.amount().multiply(VIP_MULTIPLIER);

        return new Money(newAmount.setScale(2, RoundingMode.HALF_UP), basePrice.currency());
    }
}
