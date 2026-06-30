package com.ticketing.model;

import com.ticketing.enums.EventStatus;
import com.ticketing.enums.SeatStatus;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Event Aggregate Root (clean DDD-style)
 * - Encapsulates state
 * - No Lombok exposure
 * - Business-driven behavior only
 */
public class Event {

    private final Long id;
    private final String title;
    private final Venue venue;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Money basePrice;

    private EventStatus status;

    // Use Map instead of List for fast lookup (production-style improvement)
    private final Map<Long, Seat> seats = new HashMap<>();

    // ================= CONSTRUCTOR =================
    public Event(Long id,
                 String title,
                 Venue venue,
                 LocalDateTime startTime,
                 LocalDateTime endTime,
                 Money basePrice,
                 EventStatus status,
                 List<Seat> initialSeats) {

        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.title = validateText(title, "title");
        this.venue = Objects.requireNonNull(venue, "venue cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "startTime cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "endTime cannot be null");
        this.basePrice = Objects.requireNonNull(basePrice, "basePrice cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");

        if (initialSeats != null) {
            for (Seat seat : initialSeats) {
                seats.put(seat.getId(), seat);
            }
        }
    }

    // ================= DOMAIN BEHAVIOR =================

    public void cancel() {
        ensureActive();
        this.status = EventStatus.CANCELLED;
    }

    public void complete(Clock clock) {
        if (LocalDateTime.now(clock).isBefore(endTime)) {
            throw new IllegalStateException("Event cannot be completed before end time");
        }
        this.status = EventStatus.COMPLETED;
    }

    public void activate() {
        this.status = EventStatus.ACTIVE;
    }

    // ================= SEAT MANAGEMENT =================

    public void addSeat(Seat seat) {
        ensureActive();

        Objects.requireNonNull(seat, "seat cannot be null");

        if (seats.containsKey(seat.getId())) {
            throw new IllegalStateException("Seat already exists: " + seat.getId());
        }

        seats.put(seat.getId(), seat);
    }

    public Seat getSeatById(Long seatId) {
        return seats.get(seatId);
    }

    public boolean hasSeat(Long seatId) {
        return seats.containsKey(seatId);
    }

    public List<Seat> getAvailableSeats() {
        return seats.values()
                .stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public int getTotalSeats() {
        return seats.size();
    }

    // ================= BUSINESS RULES =================

    private void ensureActive() {
        if (status != EventStatus.ACTIVE) {
            throw new IllegalStateException("Event is not ACTIVE. Current status: " + status);
        }
    }

    // ================= QUERY METHODS =================

    public boolean isActive() {
        return status == EventStatus.ACTIVE;
    }

    public boolean isCancelled() {
        return status == EventStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return status == EventStatus.COMPLETED;
    }

    // ================= VALIDATION =================

    private String validateText(String value, String field) {
        Objects.requireNonNull(value, field + " cannot be null");

        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }

        return trimmed;
    }

    // ================= GETTERS (READ-ONLY) =================

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Venue getVenue() {
        return venue;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    public EventStatus getStatus() {
        return status;
    }

    public List<Seat> getSeats() {
        return new ArrayList<>(seats.values());
    }

    // ================= TO STRING =================

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", venue=" + venue.getName() +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", basePrice=" + basePrice +
                ", status=" + status +
                ", totalSeats=" + seats.size() +
                '}';
    }
}