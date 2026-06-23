package com.ticketing.model;

import com.ticketing.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reservation {

    private Long id;
    private String customerEmail;
    private Long eventId;
    private Long seatId;
    private double finalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    // =========================
    // Constructor for creation (business use)
    // =========================
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

    // =========================
    // IDEMPOTENCY KEY
    // =========================
    public String uniqueKey() {

        return String.format(
                "%s-%d-%d",
                customerEmail == null ? "" : customerEmail.toLowerCase(),
                eventId,
                seatId
        );
    }

    // =========================
    // BUSINESS METHODS
    // =========================

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
}