package com.ticketing.enums;

/**
 * Strongly-typed pricing strategy selector.
 * Replaces unsafe string-based strategy resolution.
 */
public enum PricingStrategyType {
    VIP,
    DISCOUNT,
    EARLY_BIRD,
    STANDARD
}