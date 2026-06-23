package com.ticketing.model;

import com.ticketing.enums.EventStatus;
import com.ticketing.enums.SeatStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Event {

    private Long id;
    private String title;
    private Venue venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double basePrice;
    private EventStatus status;
    private List<Seat> seats = new ArrayList<>();

    // No-Args Constructor
    public Event() {
    }

    // All-Args Constructor
    public Event(Long id,
                 String title,
                 Venue venue,
                 double basePrice,
                 LocalDateTime startTime,
                 LocalDateTime endTime,
                 EventStatus status,
                 List<Seat> seats) {

        this.id = id;
        this.title = title;
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.basePrice = basePrice;
        this.seats = seats;
    }

    // Getters & Setters

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    // Helper Methods

    public int getTotalSeats() {
        return seats == null ? 0 : seats.size();
    }

    public Seat getSeatById(Long seatId) {

        if (seats == null) {
            return null;
        }

        return seats.stream()
                .filter(seat -> seatId.equals(seat.getId()))
                .findFirst()
                .orElse(null);
    }

    public List<Seat> getAvailableSeats() {

        if (seats == null) {
            return new ArrayList<>();
        }

        return seats.stream()
                .filter(seat ->
                        seat.getStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public boolean hasSeat(Long seatId) {
        return getSeatById(seatId) != null;
    }

    public void addSeat(Seat seat) {

        if (seats == null) {
            seats = new ArrayList<>();
        }

        seats.add(seat);
    }

    @Override
    public String toString() {

        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", venue=" +
                (venue != null
                        ? venue.getName()
                        : "N/A") +
                ", basePrice=" + basePrice +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", totalSeats=" + getTotalSeats() +
                '}';
    }
}