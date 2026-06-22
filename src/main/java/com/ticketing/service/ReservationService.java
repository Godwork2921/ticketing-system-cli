package com.ticketing.service;

import com.ticketing.dao.ReservationDAO;
import com.ticketing.dao.SeatDAO;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final SeatDAO seatDAO = new SeatDAO();

    public void reserveSeat(String email,
                            Long eventId,
                            Long seatId) {

        // 1. validate seat belongs to event
        if (!seatDAO.existsInEvent(eventId, seatId)) {
            throw new RuntimeException("Seat does not belong to event");
        }

        // 2. load seat safely
        Seat seat = seatDAO.findById(seatId);

        if (seat == null) {
            throw new RuntimeException("Seat not found");
        }

        // 3. check availability
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Seat already reserved");
        }

        // 4. prevent double booking (seat-level)
        if (reservationDAO.existsBySeatId(seatId)) {
            throw new RuntimeException("Seat already booked");
        }

        // 5. IDEMPOTENCY CHECK (customer-level)
        if (reservationDAO.exists(email, eventId, seatId)) {
            throw new RuntimeException("Reservation already exists!");
        }

        // 6. create reservation
        Reservation reservation = new Reservation(
                email,
                eventId,
                seatId,
                ReservationStatus.CONFIRMED,
                LocalDateTime.now()
        );

        // 7. save reservation
        reservationDAO.save(reservation);

        // 8. update seat status
        seatDAO.updateStatus(seatId, SeatStatus.RESERVED);
    }

    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }

    public List<Reservation> getReservationsByCustomer(String email) {
        return reservationDAO.findAll()
                .stream()
                .filter(r -> r.getCustomerEmail().equals(email))
                .toList();
    }

    public boolean cancelReservation(Long reservationId) {

        Reservation reservation =
                reservationDAO.findById(reservationId);

        if (reservation == null) {
            throw new RuntimeException("Reservation not found.");
        }

        // restore seat
        seatDAO.updateStatus(
                reservation.getSeatId(),
                SeatStatus.AVAILABLE
        );

        return reservationDAO.cancel(reservationId);
    }

    public Reservation findById(Long id) {
        return reservationDAO.findById(id);
    }
}