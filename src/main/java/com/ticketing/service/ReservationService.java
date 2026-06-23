package com.ticketing.service;

import com.ticketing.dao.ReservationDAO;
import com.ticketing.dao.SeatDAO;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final PricingService pricingService = new PricingService();
    private final EventService eventService = new EventService();
    public void reserveSeat(String email,
                            Long eventId,
                            Long seatId) {

        if (!seatDAO.existsInEvent(eventId, seatId)) {
            throw new RuntimeException("Seat does not belong to event");
        }

        Seat seat = seatDAO.findByIdForUpdate(seatId);

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Seat already reserved");
        }

        if (reservationDAO.exists(email, eventId, seatId)) {
            throw new RuntimeException("Reservation already exists!");
        }

        Event event = eventService.findById(eventId);

        // 💰 CALCULATE FINAL PRICE
        double finalPrice = pricingService.calculateFinalPrice(
                event,
                seat,
                LocalDateTime.now()
        );

        Reservation reservation = new Reservation(
                email,
                eventId,
                seatId,
                ReservationStatus.CONFIRMED,
                LocalDateTime.now()
        );

        reservation.setFinalPrice(finalPrice); // ⭐ SAVE PRICE

        reservationDAO.save(reservation);

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