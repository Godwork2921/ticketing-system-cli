package com.ticketing.factory;

import com.ticketing.strategy.*;

public class PricingStrategyFactory {

    public static PricingStrategy getStrategy(String type) {

        return switch (type.toUpperCase()) {

            case "VIP" -> new VipPricingStrategy();
            case "DISCOUNT" -> new DiscountPricingStrategy();
            case "EARLY" -> new EarlyBirdStrategy();
            default -> new StandardPricingStrategy();
        };
    }
}