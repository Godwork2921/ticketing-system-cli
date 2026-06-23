package com.ticketing.service;

import com.ticketing.concurrency.EventLockManager;
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
import java.util.concurrent.locks.ReentrantLock;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final PricingService pricingService = new PricingService();
    private final EventService eventService = new EventService();

    // ========================= MAIN FLOW =========================
    public void reserveSeat(String email, Long eventId, Long seatId) {

        ReentrantLock lock = EventLockManager.getLock(eventId);
        lock.lock();

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Validate event
            Event event = eventService.findById(eventId);
            if (event == null) {
                throw new RuntimeException("Event not found");
            }

            // 2. Validate seat belongs to event
            if (!seatDAO.existsInEvent(eventId, seatId)) {
                throw new RuntimeException("Seat does not belong to event");
            }

            // 3. Idempotency check (GLOBAL SAFETY)
            if (reservationDAO.exists(email, eventId, seatId)) {
                throw new RuntimeException("Reservation already exists (idempotent)");
            }

            // 4. Lock seat (DB-level safety)
            Seat seat = seatDAO.findByIdForUpdate(seatId);

            if (seat == null) {
                throw new RuntimeException("Seat not found");
            }

            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat already reserved");
            }

            // 5. Pricing (Strategy Pattern)
            double finalPrice = pricingService.calculateFinalPrice(
                    event,
                    seat,
                    LocalDateTime.now()
            );

            // 6. Create reservation
            Reservation reservation = new Reservation(
                    email,
                    eventId,
                    seatId,
                    ReservationStatus.CONFIRMED,
                    LocalDateTime.now()
            );

            reservation.setFinalPrice(finalPrice);

            // 7. Persist
            reservationDAO.save(reservation);
            seatDAO.updateStatus(seatId, SeatStatus.RESERVED);

            // 8. Commit
            conn.commit();

            System.out.println("[SUCCESS] Reservation completed. Price = " + finalPrice);

        } catch (Exception e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    throw new RuntimeException("Rollback failed", ex);
                }
            }

            throw new RuntimeException("Reservation failed: " + e.getMessage(), e);

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ignored) {}
            }

            lock.unlock();
        }
    }

    // ========================= READ =========================

    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }

    public List<Reservation> getReservationsByCustomer(String email) {
        return reservationDAO.findAll()
                .stream()
                .filter(r -> r.getCustomerEmail().equals(email))
                .toList();
    }

    public Reservation findById(Long id) {
        return reservationDAO.findById(id);
    }

    // ========================= CANCEL =========================

    public boolean cancelReservation(Long reservationId) {

        Reservation reservation = reservationDAO.findById(reservationId);

        if (reservation == null) {
            throw new RuntimeException("Reservation not found");
        }

        ReentrantLock lock = EventLockManager.getLock(reservation.getEventId());

        lock.lock();

        try {
            seatDAO.updateStatus(reservation.getSeatId(), SeatStatus.AVAILABLE);
            return reservationDAO.cancel(reservationId);

        } finally {
            lock.unlock();
        }
    }
}