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
    private LocalDateTime expiresAt;

    // ==========================================
    // Constructor (Business creation)
    // ==========================================
    public Reservation(
            String customerEmail,
            Long eventId,
            Long seatId,
            ReservationStatus status,
            LocalDateTime createdAt
    ) {
        this.customerEmail = customerEmail;
        this.eventId = eventId;
        this.seatId = seatId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ==========================================
    // HOLD STATE (Temporary Reservation)
    // ==========================================
    public void holdForMinutes(int minutes) {

        this.status = ReservationStatus.HOLDING;

        this.expiresAt = LocalDateTime.now()
                .plusMinutes(minutes);
    }

    // ==========================================
    // EXPIRED CHECK (TTL LOGIC)
    // ==========================================
    public boolean isExpired() {

        return expiresAt != null
                && LocalDateTime.now().isAfter(expiresAt);
    }

    // ==========================================
    // CONFIRM RESERVATION
    // ==========================================
    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
        this.expiresAt = null;
    }

    // ==========================================
    // IDEMPOTENCY KEY
    // ==========================================
    public String uniqueKey() {

        return String.format(
                "%s-%d-%d",
                customerEmail == null ? "" : customerEmail.toLowerCase(),
                eventId,
                seatId
        );
    }

    // ==========================================
    // BUSINESS HELPERS
    // ==========================================
    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    public boolean isHolding() {
        return status == ReservationStatus.HOLDING;
    }

    public boolean belongsToCustomer(String email) {
        return email != null
                && email.equalsIgnoreCase(customerEmail);
    }

    public boolean belongsToEvent(Long eventId) {
        return Objects.equals(this.eventId, eventId);
    }
}