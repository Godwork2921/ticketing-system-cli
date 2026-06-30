package com.ticketing.factory;

import com.ticketing.enums.PricingStrategyType;
import com.ticketing.strategy.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Production-grade Pricing Strategy Factory.
 *
 * Improvements over switch-based version:
 * - No switch statement (more scalable)
 * - O(1) lookup via EnumMap
 * - Easy to extend without modifying logic (Open/Closed Principle)
 * - No magic strings
 */
public final class PricingStrategyFactory {

    private static final Map<PricingStrategyType, Supplier<PricingStrategy>> STRATEGIES =
            new EnumMap<>(PricingStrategyType.class);

    static {
        STRATEGIES.put(PricingStrategyType.VIP, VipPricingStrategy::new);
        STRATEGIES.put(PricingStrategyType.DISCOUNT, DiscountPricingStrategy::new);
        STRATEGIES.put(PricingStrategyType.EARLY_BIRD, EarlyBirdStrategy::new);
        STRATEGIES.put(PricingStrategyType.STANDARD, StandardPricingStrategy::new);
    }

    private PricingStrategyFactory() {
        // prevent instantiation
    }

    public static PricingStrategy getStrategy(PricingStrategyType type) {

        Supplier<PricingStrategy> supplier = STRATEGIES.get(type);

        if (supplier == null) {
            throw new IllegalArgumentException("Unsupported pricing strategy: " + type);
        }

        return supplier.get();
    }
}