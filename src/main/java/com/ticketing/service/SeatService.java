package com.ticketing.service;

import com.ticketing.dao.SeatDAO;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Seat;

import java.util.List;

public class SeatService {


        private final SeatDAO seatDAO = new SeatDAO();

        public void createSeat(Long eventId, Seat seat) {
            seatDAO.save(eventId, seat);
        }

        public Seat findById(Long id) {
            return seatDAO.findById(id);
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