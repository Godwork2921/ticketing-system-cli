package com.ticketing.model;

import com.ticketing.enums.SeatStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Getter
@NoArgsConstructor
@ToString
public class Seat {

    private Long id;
    private Long eventId;

    private String section;
    private String row;
    private int number;

    private SeatStatus status;

    // =====================================================
    // BUSINESS CONSTRUCTOR (USED IN APP LOGIC)
    // =====================================================
    public Seat(Long eventId,
                String section,
                String row,
                int number) {

        this.eventId = Objects.requireNonNull(eventId);
        this.section = validateText(section, "section");
        this.row = validateText(row, "row");
        this.number = validateNumber(number);

        this.status = SeatStatus.AVAILABLE;
    }

    // =====================================================
    // PERSISTENCE CONSTRUCTOR (USED BY DAO ONLY)
    // =====================================================
    public Seat(Long id,
                Long eventId,
                String section,
                String row,
                int number,
                SeatStatus status) {

        this.id = id;
        this.eventId = eventId;
        this.section = section;
        this.row = row;
        this.number = number;
        this.status = status;
    }

    // =====================================================
    // BUSINESS METHODS
    // =====================================================
    public void reserve() {
        if (status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("Seat not available: " + status);
        }
        this.status = SeatStatus.RESERVED;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }

    public void block() {
        if (status == SeatStatus.RESERVED) {
            throw new IllegalStateException("Cannot block reserved seat");
        }
        this.status = SeatStatus.BLOCKED;
    }

    // =====================================================
    // QUERIES
    // =====================================================
    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return status == SeatStatus.RESERVED;
    }

    public String getSeatLabel() {
        return section + "-" + row + "-" + number;
    }

    // =====================================================
    // VALIDATION
    // =====================================================
    private String validateText(String value, String field) {

        Objects.requireNonNull(value, field + " cannot be null");

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }

        return value;
    }

    private int validateNumber(int number) {

        if (number <= 0) {
            throw new IllegalArgumentException("Seat number must be positive");
        }

        return number;
    }
}