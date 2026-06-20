package com.ticketing.ui.controller;

import com.ticketing.enums.EventStatus;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Seat;
import com.ticketing.model.Venue;
import com.ticketing.util.AppContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OperatorController {

    public void createVenue(
            Long venueId,
            String venueName,
            String address,
            String timezone
    ) {

        Venue venue =
                new Venue(
                        venueId,
                        venueName,
                        address,
                        timezone
                );

        AppContext.venueService
                .addVenue(venue);

        System.out.println(
                "Venue created successfully."
        );
    }

    public void listVenues() {

        AppContext.venueService
                .getAllVenues()
                .forEach(System.out::println);
    }

    public void createEvent(
            Long eventId,
            String title,
            Long venueId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        if (startTime.isAfter(endTime)) {

            System.out.println(
                    "Invalid time range."
            );

            return;
        }

        Venue venue =
                AppContext.venueService
                        .findVenueById(venueId);

        Event event =
                new Event(
                        eventId,
                        title,
                        venue,
                        startTime,
                        endTime,
                        EventStatus.ACTIVE,
                        new ArrayList<>()
                );

        AppContext.eventService
                .createEvent(event);

        System.out.println(
                "Event created successfully."
        );
    }

    public void listEvents() {

        AppContext.eventService
                .getAllEvents()
                .forEach(System.out::println);
    }

    public void createSeat(
            Long eventId,
            Long seatId,
            String section,
            String row,
            int seatNumber
    ) {

        Event event =
                AppContext.eventService
                        .findById(eventId);

        if (event == null) {

            throw new RuntimeException(
                    "Event not found."
            );
        }

        Seat seat =
                new Seat(
                        seatId,
                        eventId,
                        section,
                        row,
                        seatNumber,
                        SeatStatus.AVAILABLE
                );

        AppContext.seatService
                .createSeat(
                        eventId,
                        seat
                );

        System.out.println(
                "Seat added successfully."
        );
    }

    public void viewAllAvailableSeats(Long eventId) {

        List<Seat> seats = AppContext.seatService.getAvailableSeats(eventId);

        if (seats.isEmpty()) {
            System.out.println("No available seats found.");
            return;
        }

        System.out.println("\n===== AVAILABLE SEATS =====");
        seats.forEach(System.out::println);
    }

    public void viewAllReservedSeats(Long eventId) {

        List<Seat> seats = AppContext.seatService.getReservedSeats(eventId);

        if (seats.isEmpty()) {
            System.out.println("No reserved seats found.");
            return;
        }

        System.out.println("\n===== RESERVED SEATS =====");
        seats.forEach(System.out::println);
    }
}