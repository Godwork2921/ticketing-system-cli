package com.ticketing.model;

import com.ticketing.enums.ReservationStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Objects;

public class Reservation {

    // =====================================================
    // CORE IDENTITY (IMMUTABLE)
    // =====================================================
    private final Long id;
    private final String customerEmail;
    private final Long eventId;
    private final Long seatId;
    private final LocalDateTime createdAt;

    // =====================================================
    // MUTABLE STATE
    // =====================================================
    private ReservationStatus status;
    private Money finalPrice;
    private LocalDateTime expiresAt;

    // =====================================================
    // BUSINESS CONSTRUCTOR
    // =====================================================
    public Reservation(String customerEmail,
                       Long eventId,
                       Long seatId,
                       ReservationStatus status,
                       LocalDateTime createdAt) {

        this.id = null;
        this.customerEmail = Objects.requireNonNull(customerEmail);
        this.eventId = Objects.requireNonNull(eventId);
        this.seatId = Objects.requireNonNull(seatId);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    // =====================================================
    // DAO CONSTRUCTOR
    // =====================================================
    public Reservation(Long id,
                       String customerEmail,
                       Long eventId,
                       Long seatId,
                       ReservationStatus status,
                       LocalDateTime createdAt,
                       Money finalPrice,
                       LocalDateTime expiresAt) {

        this.id = id;
        this.customerEmail = Objects.requireNonNull(customerEmail);
        this.eventId = Objects.requireNonNull(eventId);
        this.seatId = Objects.requireNonNull(seatId);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.finalPrice = finalPrice;
        this.expiresAt = expiresAt;
    }

    // =====================================================
    // DOMAIN BEHAVIOR
    // =====================================================

    public void holdForMinutes(int minutes, Clock clock) {
        this.status = ReservationStatus.HOLDING;
        this.expiresAt = LocalDateTime.now(clock).plusMinutes(minutes);
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
        this.expiresAt = null;
    }

    public void holdForSeconds(LocalDateTime now, long ttlSeconds) {

        if (status == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Confirmed reservation cannot be put on hold."
            );
        }

        this.status = ReservationStatus.HOLDING;
        this.expiresAt = now.plusSeconds(ttlSeconds);
    }

    public boolean isExpired(Clock clock) {
        return expiresAt != null &&
                LocalDateTime.now(clock).isAfter(expiresAt);
    }

    public void assignFinalPrice(Money price) {
        this.finalPrice = Objects.requireNonNull(price);
    }

    // =====================================================
    // IDEMPOTENCY KEY (STRONG)
    // =====================================================
    public String uniqueKey() {
        try {
            String raw = customerEmail.trim().toLowerCase()
                    + "|" + eventId
                    + "|" + seatId;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }


    // =====================================================
    // QUERIES
    // =====================================================
    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    public boolean isHolding() {
        return status == ReservationStatus.HOLDING;
    }

    public boolean belongsToCustomer(String email) {
        return email != null &&
                customerEmail.equalsIgnoreCase(email.trim());
    }

    // =====================================================
    // GETTERS ONLY
    // =====================================================
    public Long getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public Long getEventId() { return eventId; }
    public Long getSeatId() { return seatId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public ReservationStatus getStatus() { return status; }
    public Money getFinalPrice() { return finalPrice; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}