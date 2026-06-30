package com.ticketing.model;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {
    @Override
    public String toString() {
        return amount + " " + currency.getCurrencyCode();
    }
}
