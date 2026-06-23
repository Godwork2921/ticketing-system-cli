package com.ticketing.factory;

import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Seat;

public class SeatFactory {

    private SeatFactory() {
        // prevent instantiation
    }

    public static Seat createSeat(
            Long id,
            Long eventId,
            String section,
            String row,
            int number
    ) {

        validateInput(eventId, section, row, number);

        return new Seat(
                id,
                eventId,
                section,
                row,
                number,
                SeatStatus.AVAILABLE
        );
    }

    private static void validateInput(
            Long eventId,
            String section,
            String row,
            int number
    ) {

        if (eventId == null) {
            throw new IllegalArgumentException("EventId cannot be null");
        }

        if (section == null || section.isBlank()) {
            throw new IllegalArgumentException("Section cannot be empty");
        }

        if (row == null || row.isBlank()) {
            throw new IllegalArgumentException("Row cannot be empty");
        }

        if (number <= 0) {
            throw new IllegalArgumentException("Seat number must be > 0");
        }
    }
}