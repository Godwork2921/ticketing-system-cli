package com.ticketing.service;

import com.ticketing.concurrency.SeatLockManager;
import com.ticketing.dao.ReservationDAO;
import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Money;
import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.service.pricing.PricingService;

import java.sql.Connection;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final PricingService pricingService = new PricingService();
    private final EventService eventService = new EventService();
    private final Clock clock;

    // ===================== CONSTRUCTOR =====================
    public ReservationService(Clock clock) {
        this.clock = clock;
    }

    public ReservationService() {
        this(Clock.systemDefaultZone());
    }

    // =====================================================
    // RESERVE SEAT (ATOMIC + SAFE)
    // =====================================================
    public boolean reserveSeat(String email, Long eventId, Long seatId) {

        ReentrantLock lock = SeatLockManager.getLock(eventId, seatId);
        lock.lock();

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                // 1. Load event
                Event event = eventService.findById(eventId);
                if (event == null) {
                    conn.rollback();
                    return false;
                }

                // 2. Idempotency check
                if (reservationDAO.exists(conn, email, eventId, seatId)) {
                    conn.rollback();
                    return false;
                }

                // 3. Atomic seat update (THIS IS THE REAL GATE)
                int updated = seatDAO.reserveIfAvailable(conn, seatId);
                if (updated != 1) {
                    conn.rollback();
                    return false;
                }

                // 4. Load seat
                Seat seat = seatDAO.findById(conn, seatId);
                if (seat == null) {
                    conn.rollback();
                    return false;
                }

                // 5. Time (CLOCK FIXED)
                LocalDateTime now = LocalDateTime.now(clock);

                // 6. Pricing (Money object)
                Money price = pricingService.calculateFinalPrice(event, seat, now);

                // 7. Create reservation (correct constructor)
                Reservation reservation = new Reservation(
                        email,
                        eventId,
                        seatId,
                        ReservationStatus.CONFIRMED,
                        now
                );

                // 8. IMPORTANT: assign Money (you were missing this)
                reservation.assignFinalPrice(price);
                // 9. Persist
                reservationDAO.save(conn, reservation);

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Reservation failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            lock.unlock();
        }
    }

    // =====================================================
    // CANCEL RESERVATION
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

                ReentrantLock lock = SeatLockManager.getLock(
                        reservation.getEventId(),
                        reservation.getSeatId()
                );

                lock.lock();

                try {
                    boolean seatOk = seatDAO.updateStatus(
                            conn,
                            reservation.getSeatId(),
                            SeatStatus.AVAILABLE
                    );

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
                throw new RuntimeException("Cancel failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    // =====================================================
    // READ OPERATIONS
    // =====================================================
    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }

    public List<Reservation> getReservationsByCustomer(String email) {
        return reservationDAO.findAll()
                .stream()
                .filter(r -> r.getCustomerEmail().equalsIgnoreCase(email))
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