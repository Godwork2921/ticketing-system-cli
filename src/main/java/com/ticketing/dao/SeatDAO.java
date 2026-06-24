package com.ticketing.dao;

import com.ticketing.cache.SeatCache;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    // =====================================================
    // SAVE SEAT
    // =====================================================
    public void save(Long eventId, Seat seat) {

        String sql = """
            INSERT INTO seats
            (id, event_id, section, row_name, seat_number, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = com.ticketing.database.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, seat.getId());
            ps.setLong(2, eventId);
            ps.setString(3, seat.getSection());
            ps.setString(4, seat.getRow());
            ps.setInt(5, seat.getNumber());
            ps.setString(6, seat.getStatus().name());

            ps.executeUpdate();

            SeatCache.put(seat);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save seat", e);
        }
    }

    // =====================================================
    // ATOMIC RESERVE (IMPORTANT FOR CONCURRENCY)
    // =====================================================
    public int reserveIfAvailable(Connection conn, Long seatId) throws SQLException {

        String sql = """
            UPDATE seats
            SET status = ?
            WHERE id = ? AND status = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, SeatStatus.RESERVED.name());
            ps.setLong(2, seatId);
            ps.setString(3, SeatStatus.AVAILABLE.name());

            return ps.executeUpdate(); // MUST be 1 or 0
        }
    }

    // =====================================================
    // FIND BY ID (TRANSACTION SAFE)
    // =====================================================
    public Seat findById(Connection conn, Long id) throws SQLException {

        String sql = "SELECT * FROM seats WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;
        }
    }

    // =====================================================
    // EXISTS IN EVENT
    // =====================================================
    public boolean existsInEvent(Long eventId, Long seatId) {

        String sql = """
            SELECT 1 FROM seats
            WHERE event_id = ? AND id = ?
        """;

        try (Connection conn = com.ticketing.database.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eventId);
            ps.setLong(2, seatId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("existsInEvent failed", e);
        }
    }

    public void resetSeat(Long seatId) throws SQLException {

        String sql = "UPDATE seats SET status = 'AVAILABLE' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, seatId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Seat not found: " + seatId);
            }
        }
    }
    // =====================================================
    // FIND ALL SEATS
    // =====================================================
    public List<Seat> findAll() {

        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seats";

        try (Connection conn = com.ticketing.database.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                seats.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch seats", e);
        }

        return seats;
    }

    // =====================================================
    // FIND BY EVENT
    // =====================================================
    public List<Seat> findByEventId(Long eventId) {

        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seats WHERE event_id = ?";

        try (Connection conn = com.ticketing.database.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return seats;
    }

    // =====================================================
    // FIND AVAILABLE SEATS
    // =====================================================
    public List<Seat> findAvailableSeats(Long eventId) {

        List<Seat> seats = new ArrayList<>();

        String sql = """
            SELECT * FROM seats
            WHERE event_id = ? AND status = 'AVAILABLE'
        """;

        try (Connection conn = com.ticketing.database.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return seats;
    }

    public Seat findByIdForUpdate(Connection conn, Long seatId) {

        String sql = """
        SELECT * FROM seats
        WHERE id = ?
        FOR UPDATE
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, seatId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Seat seat = new Seat();

                seat.setId(rs.getLong("id"));
                seat.setEventId(rs.getLong("event_id"));
                seat.setSection(rs.getString("section"));
                seat.setRow(rs.getString("row_name"));
                seat.setNumber(rs.getInt("seat_number"));
                seat.setStatus(
                        SeatStatus.valueOf(
                                rs.getString("status")
                        )
                );

                return seat;
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to lock seat", e);
        }
    }

    public Seat findById(Long id) {
        try (Connection conn = DBConnection.getConnection()) {
            return findById(conn, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // =====================================================
    // FIND RESERVED SEATS
    // =====================================================
    public List<Seat> findReservedSeats(Long eventId) {

        List<Seat> seats = new ArrayList<>();

        String sql = """
            SELECT * FROM seats
            WHERE event_id = ? AND status = 'RESERVED'
        """;

        try (Connection conn = com.ticketing.database.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return seats;
    }

    public boolean updateStatus(Connection conn, Long seatId, SeatStatus status) throws SQLException {

        String sql = "UPDATE seats SET status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setLong(2, seatId);

            int rows = ps.executeUpdate();

            return rows == 1;
        }
    }
    // =====================================================
    // DELETE ALL (TEST ONLY)
    // =====================================================
    public void deleteAll() {

        try (Connection conn = com.ticketing.database.DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate("DELETE FROM reservations");
            st.executeUpdate("DELETE FROM seats");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear DB", e);
        }
    }

    // =====================================================
    // MAPPER
    // =====================================================
    private Seat mapRow(ResultSet rs) throws SQLException {

        return new Seat(
                rs.getLong("id"),
                rs.getLong("event_id"),
                rs.getString("section"),
                rs.getString("row_name"),
                rs.getInt("seat_number"),
                SeatStatus.valueOf(rs.getString("status"))
        );
    }
}