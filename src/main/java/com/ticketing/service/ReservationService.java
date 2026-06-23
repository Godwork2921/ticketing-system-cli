package com.ticketing.service;

import com.ticketing.dao.ReservationDAO;
import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;

import java.sql.Connection;
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

        Connection conn = null;

        try {

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Validate seat belongs to event
            if (!seatDAO.existsInEvent(eventId, seatId)) {
                throw new RuntimeException("Seat does not belong to event");
            }

            // 2. LOCK SEAT (VERY IMPORTANT)
            Seat seat = seatDAO.findByIdForUpdate(seatId);

            if (seat == null) {
                throw new RuntimeException("Seat not found");
            }

            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat already reserved");
            }

            // 3. Idempotency check
            if (reservationDAO.exists(email, eventId, seatId)) {
                throw new RuntimeException("Reservation already exists!");
            }

            // 4. Get event
            Event event = eventService.findById(eventId);

            // 5. Pricing (INSIDE TRANSACTION)
            double finalPrice = pricingService.calculateFinalPrice(
                    event,
                    seat,
                    LocalDateTime.now()
            );

            System.out.printf("Final Ticket Price = %.2f%n", finalPrice);

            // 6. Create reservation
            Reservation reservation = new Reservation(
                    email,
                    eventId,
                    seatId,
                    ReservationStatus.CONFIRMED,
                    LocalDateTime.now()
            );

            reservation.setFinalPrice(finalPrice);

            // 7. Save reservation
            reservationDAO.save(reservation);

            // 8. Update seat
            seatDAO.updateStatus(seatId, SeatStatus.RESERVED);

            // 9. COMMIT
            conn.commit();

        } catch (Exception e) {

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Rollback failed", rollbackEx);
            }

            throw new RuntimeException("Reservation failed: " + e.getMessage(), e);

        } finally {

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
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

        Reservation reservation = reservationDAO.findById(reservationId);

        if (reservation == null) {
            throw new RuntimeException("Reservation not found.");
        }

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