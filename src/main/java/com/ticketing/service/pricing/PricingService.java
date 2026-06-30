package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Money;
import com.ticketing.model.Seat;

import java.time.LocalDateTime;
import java.util.List;

public class PricingService {

    private final List<PricingStrategy> strategies;

    public PricingService() {
        this.strategies = List.of(
                new VIPPricingStrategy(),
                new TimeDiscountStrategy()
        );
    }

    public Money calculateFinalPrice(Event event,
                                     Seat seat,
                                     LocalDateTime time) {

        Money price = event.getBasePrice();

        for (PricingStrategy strategy : strategies) {
            price = strategy.apply(price, event, seat, time);
        }

        return price;
    }
}
