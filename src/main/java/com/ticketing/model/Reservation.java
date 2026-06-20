package com.ticketing.model;

import com.ticketing.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {

    private Long id;
    private String customerEmail;
    private Long eventId;
    private Long seatId;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    // No-Args Constructor (required for Jackson)
    public Reservation() {}

    // Constructor for new reservation creation
    public Reservation(String customerEmail,
                       Long eventId,
                       Long seatId,
                       ReservationStatus status,
                       LocalDateTime createdAt) {
        this.customerEmail = customerEmail;
        this.eventId = eventId;
        this.seatId = seatId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ================= UNIQUE KEY (IMPORTANT FIX) =================

    public String uniqueKey() {
        return String.format("%s-%d-%d",
                customerEmail == null ? "" : customerEmail.toLowerCase(),
                eventId,
                seatId
        );
    }

    // ================= BUSINESS METHODS =================

    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    public boolean belongsToCustomer(String email) {
        return email != null &&
                email.equalsIgnoreCase(customerEmail);
    }

    public boolean belongsToEvent(Long eventId) {
        return Objects.equals(this.eventId, eventId);
    }

    // ================= DISPLAY =================

    @Override
    public String toString() {
        return """
                Reservation
                ------------------------
                ID: %d
                Customer: %s
                Event ID: %d
                Seat ID: %d
                Status: %s
                Created At: %s
                """.formatted(
                id,
                customerEmail,
                eventId,
                seatId,
                status,
                createdAt
        );
    }
}