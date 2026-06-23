package com.ticketing.service.pricing;

import com.ticketing.model.Event;
import com.ticketing.model.Seat;

import java.time.LocalDateTime;
import java.util.List;

public class PricingService {

    private final List<PricingStrategy> strategies;

    public PricingService() {
        this.strategies = List.of(
                new BasePricingStrategy(),
                new VIPPricingStrategy(),
                new TimeDiscountStrategy()
        );
    }

    public double calculateFinalPrice(Event event,
                                      Seat seat,
                                      LocalDateTime time) {

        double price = event.getBasePrice();

        for (PricingStrategy strategy : strategies) {
            price = strategy.calculate(event, seat, time);
        }

        return price;
    }
}