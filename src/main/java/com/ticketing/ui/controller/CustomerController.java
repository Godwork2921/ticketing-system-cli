package com.ticketing.ui.controller;

import com.ticketing.model.Event;
import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.session.Session;
import com.ticketing.util.AppContext;

import java.util.List;

public class CustomerController {

    /**
     * View all events
     */
    public List<Event> getAllEvents() {
        return AppContext.eventService.getAllEvents();
    }

    /**
     * Get event by ID
     */
    public Event getEvent(Long eventId) {

        Event event =
                AppContext.eventService.findById(eventId);

        if (event == null) {
            throw new RuntimeException("Event not found.");
        }

        return event;
    }

    /**
     * View available seats
     */
    public void viewAvailableSeats(Long eventId) {

        List<Seat> seats =
                AppContext.seatService
                        .getAvailableSeats(eventId);

        if (seats.isEmpty()) {

            System.out.println(
                    "No available seats."
            );

            return;
        }

        System.out.println(
                "\n===== AVAILABLE SEATS ====="
        );

        seats.forEach(System.out::println);
    }

    /**
     * Get available seats list
     */
    public List<Seat> getAvailableSeats(Long eventId) {

        return AppContext.seatService
                .getAvailableSeats(eventId);
    }

    /**
     * Reserve seat
     */
    public void reserveSeat(
            String email,
            Long eventId,
            Long seatId
    ) {

        Event event = getEvent(eventId);

        if (event == null) {
            throw new RuntimeException("Event not found.");
        }

        Seat seat =
                AppContext.seatService
                        .findById(seatId);

        if (seat == null) {
            throw new RuntimeException("Seat not found.");
        }

        AppContext.reservationService
                .reserveSeat(
                        email,
                        eventId,
                        seatId
                );

        System.out.println(
                "Reservation successful."
        );
    }

    /**
     * View current user's reservations
     */
    public void viewMyReservations() {

        if (Session.getCurrentUser() == null) {

            System.out.println(
                    "No user logged in."
            );

            return;
        }

        String email =
                Session.getCurrentUser()
                        .getEmail();

        List<Reservation> reservations =
                AppContext.reservationService
                        .getReservationsByCustomer(
                                email
                        );

        if (reservations.isEmpty()) {

            System.out.println(
                    "No reservations found."
            );

            return;
        }

        System.out.println(
                "\n===== MY RESERVATIONS ====="
        );

        reservations.forEach(
                System.out::println
        );
    }

    /**
     * Cancel reservation
     */
    public void cancelReservation(
            Long reservationId
    ) {

        boolean cancelled =
                AppContext.reservationService
                        .cancelReservation(
                                reservationId
                        );

        if (cancelled) {

            System.out.println(
                    "Reservation cancelled successfully."
            );

        } else {

            System.out.println(
                    "Reservation not found."
            );
        }
    }

    /**
     * View all reservations
     */
    public List<Reservation> getAllReservations() {

        return AppContext.reservationService
                .getAllReservations();
    }
}