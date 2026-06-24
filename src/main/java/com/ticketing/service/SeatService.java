package com.ticketing.service;

import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.model.Seat;

import java.sql.Connection;
import java.util.List;

public class SeatService {

    private final SeatDAO seatDAO =
            new SeatDAO();

    public void createSeat(Long eventId, Seat seat) {

        seatDAO.save(eventId, seat);
    }

    public Seat findById(Long id) {

        try (Connection conn =
                     DBConnection.getConnection()) {

            return seatDAO.findById(
                    conn,
                    id
            );

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    public List<Seat> getSeatsByEvent(Long eventId) {

        return seatDAO.findByEventId(eventId);
    }

    public List<Seat> getAvailableSeats(Long eventId) {

        return seatDAO.findAvailableSeats(eventId);
    }

    public List<Seat> getReservedSeats(Long eventId) {

        return seatDAO.findReservedSeats(eventId);
    }
}