package com.ticketing.model;

import com.ticketing.enums.EventStatus;
import com.ticketing.enums.SeatStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Event {

    private Long id;
    private String title;
    private Venue venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double basePrice;
    private EventStatus status;
    private List<Seat> seats = new ArrayList<>();

    // ================= Helper Methods =================

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
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
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
}