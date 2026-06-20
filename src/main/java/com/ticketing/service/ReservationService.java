package com.ticketing.service;

import com.ticketing.dao.ReservationDAO;
import com.ticketing.dao.SeatDAO;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Reservation;

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

        // 2. check availability
        if (seatDAO.findById(seatId).getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Seat already reserved");
        }

        // 3. prevent double booking
        if (reservationDAO.existsBySeatId(seatId)) {
            throw new RuntimeException("Seat already booked");
        }

        // 4. create reservation (NO ID)
        Reservation reservation = new Reservation(
                email,
                eventId,
                seatId,
                ReservationStatus.CONFIRMED,
                LocalDateTime.now()
        );

        // 5. save
        reservationDAO.save(reservation);

        // 6. update seat
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

    public boolean cancelReservation(
            Long reservationId
    ) {

        Reservation reservation =
                reservationDAO.findById(
                        reservationId
                );

        if (reservation == null) {

            throw new RuntimeException(
                    "Reservation not found."
            );
        }

        seatDAO.updateStatus(
                reservation.getSeatId(),
                SeatStatus.AVAILABLE
        );

        return reservationDAO.cancel(
                reservationId
        );
    }
    public Reservation findById(Long id) {
        return reservationDAO.findById(id);
    }
    
}