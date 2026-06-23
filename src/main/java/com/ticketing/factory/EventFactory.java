package com.ticketing.factory;

import com.ticketing.enums.EventStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Venue;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventFactory {

    private EventFactory() {
        // prevent instantiation
    }

    public static Event createEvent(
            Long id,
            String title,
            Venue venue,
            double basePrice,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        validateInput(title, venue, basePrice, startTime, endTime);

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
            double basePrice,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Event title cannot be empty");
        }

        if (venue == null) {
            throw new IllegalArgumentException("Venue cannot be null");
        }

        if (basePrice <= 0) {
            throw new IllegalArgumentException("Base price must be greater than 0");
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Event time cannot be null");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}