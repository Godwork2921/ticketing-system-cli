package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Money;
import com.ticketing.model.Seat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VIPPricingStrategy implements PricingStrategy {

    private static final BigDecimal VIP_MULTIPLIER = BigDecimal.valueOf(2.5);

    @Override
    public Money apply(Money basePrice, Event event, Seat seat, LocalDateTime time) {
        if ("VIP".equalsIgnoreCase(seat.getSection())) {
            BigDecimal newAmount = basePrice.amount().multiply(VIP_MULTIPLIER);
            return new Money(newAmount.setScale(2, BigDecimal.ROUND_HALF_UP), basePrice.currency());
        }
        return basePrice;
    }
}
