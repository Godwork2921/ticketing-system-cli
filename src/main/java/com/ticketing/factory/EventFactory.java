package com.ticketing.factory;

import com.ticketing.enums.EventStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Venue;
import com.ticketing.model.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Currency;

public class EventFactory {

    private EventFactory() {
        // prevent instantiation
    }

    public static Event createEvent(
            Long id,
            String title,
            Venue venue,
            BigDecimal amount,
            String currencyCode,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        validateInput(title, venue, amount, currencyCode, startTime, endTime);

        Money basePrice = new Money(amount.setScale(2, java.math.RoundingMode.HALF_UP),
                Currency.getInstance(currencyCode));

        return new Event(
                id,
                title,
                venue,
                startTime,
                endTime,
                basePrice,
                EventStatus.ACTIVE,
                new ArrayList<>()
        );
    }

    private static void validateInput(
            String title,
            Venue venue,
            BigDecimal amount,
            String currencyCode,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Event title cannot be empty");
        }

        if (venue == null) {
            throw new IllegalArgumentException("Venue cannot be null");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base price must be greater than 0");
        }

        try {
            Currency.getInstance(currencyCode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid currency code");
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Event time cannot be null");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}
