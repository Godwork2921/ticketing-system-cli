package com.ticketing.service;

import com.ticketing.concurrency.EventLockManager;
import com.ticketing.dao.ReservationDAO;
import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class HoldService {

    private final ReservationDAO reservationDAO =
            new ReservationDAO();

    private final SeatDAO seatDAO =
            new SeatDAO();

    public void holdSeat(
            String email,
            Long eventId,
            Long seatId,
            long ttlSeconds
    ) {

        ReentrantLock lock =
                EventLockManager.getLock(eventId);

        lock.lock();

        try (Connection conn =
                     DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            // Idempotency
            if (reservationDAO.exists(
                    conn,
                    email,
                    eventId,
                    seatId
            )) {

                conn.rollback();

                throw new RuntimeException(
                        "Hold already exists"
                );
            }

            // Lock seat
            Seat seat =
                    seatDAO.findByIdForUpdate(
                            conn,
                            seatId
                    );

            if (seat == null) {

                conn.rollback();

                throw new RuntimeException(
                        "Seat not found"
                );
            }

            if (seat.getStatus() != SeatStatus.AVAILABLE) {

                conn.rollback();

                throw new RuntimeException(
                        "Seat not available"
                );
            }

            // Create hold
            Reservation hold =
                    new Reservation(
                            email,
                            eventId,
                            seatId,
                            ReservationStatus.HOLDING,
                            LocalDateTime.now()
                    );

            hold.setExpiresAt(
                    LocalDateTime.now()
                            .plusSeconds(ttlSeconds)
            );

            reservationDAO.save(
                    conn,
                    hold
            );

            seatDAO.updateStatus(
                    conn,
                    seatId,
                    SeatStatus.HELD
            );

            conn.commit();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to hold seat",
                    e
            );

        } finally {

            lock.unlock();
        }
    }
}