package com.ticketing.service;
import com.ticketing.service.pricing.PricingService;
import com.ticketing.service.EventService;
import com.ticketing.concurrency.SeatLockManager;
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

    // =====================================================
    // RESERVE SEAT (FIXED - FULL ATOMIC SAFETY)
    // =====================================================
    public boolean reserveSeat(String email, Long eventId, Long seatId) {

        ReentrantLock lock = SeatLockManager.getLock(eventId, seatId);
        lock.lock();

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                Event event = eventService.findById(eventId);
                if (event == null) {
                    conn.rollback();
                    return false;
                }

                if (reservationDAO.exists(conn, email, eventId, seatId)) {
                    conn.rollback();
                    return false;
                }

                // 🔥 ATOMIC DB LOCKING STEP
                int updated = seatDAO.reserveIfAvailable(conn, seatId);

                if (updated != 1) {
                    conn.rollback();
                    return false;
                }

                Seat seat = seatDAO.findById(conn, seatId);

                double price = pricingService.calculateFinalPrice(
                        event,
                        seat,
                        LocalDateTime.now()
                );

                Reservation r = new Reservation(
                        email,
                        eventId,
                        seatId,
                        ReservationStatus.CONFIRMED,
                        LocalDateTime.now()
                );

                r.setFinalPrice(price);

                reservationDAO.save(conn, r);

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            lock.unlock();
        }
    }
    // =====================================================
    // CANCEL RESERVATION (FIXED - TRANSACTION SAFE)
    // =====================================================
    public boolean cancelReservation(Long reservationId) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                Reservation reservation = reservationDAO.findById(conn, reservationId);
                if (reservation == null) {
                    conn.rollback();
                    return false;
                }

                Long seatId = reservation.getSeatId();
                Long eventId = reservation.getEventId();

                ReentrantLock lock = SeatLockManager.getLock(eventId, seatId);
                lock.lock();

                try {

                    // 1. Free seat
                    boolean seatOk = seatDAO.updateStatus(conn, seatId, SeatStatus.AVAILABLE);

                    // 2. Cancel reservation
                    boolean resOk = reservationDAO.cancel(conn, reservationId);

                    if (!seatOk || !resOk) {
                        conn.rollback();
                        return false;
                    }

                    conn.commit();
                    return true;

                } finally {
                    lock.unlock();
                }

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Cancel reservation failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =====================================================
    // OTHER METHODS
    // =====================================================
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

        try (Connection conn = DBConnection.getConnection()) {
            return reservationDAO.findById(conn, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}