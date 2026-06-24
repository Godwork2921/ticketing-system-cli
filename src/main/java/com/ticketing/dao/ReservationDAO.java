package com.ticketing.dao;

import com.ticketing.database.DBConnection;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // =====================================================
    // SAVE
    // =====================================================
    public void save(Connection conn, Reservation r) {

        String sql = """
            INSERT INTO reservations
            (customer_email, event_id, seat_id, status, created_at, final_price)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getCustomerEmail());
            ps.setLong(2, r.getEventId());
            ps.setLong(3, r.getSeatId());
            ps.setString(4, r.getStatus().name());
            ps.setTimestamp(5, Timestamp.valueOf(r.getCreatedAt()));
            ps.setDouble(6, r.getFinalPrice());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to save reservation", e);
        }
    }

    // =====================================================
    // EXISTS
    // =====================================================
    public boolean exists(Connection conn, String email, Long eventId, Long seatId) {

        String sql = """
            SELECT 1 FROM reservations
            WHERE customer_email=? AND event_id=? AND seat_id=?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setLong(2, eventId);
            ps.setLong(3, seatId);

            return ps.executeQuery().next();

        } catch (Exception e) {
            throw new RuntimeException("Exists check failed", e);
        }
    }

    // =====================================================
    // FIND BY ID
    // =====================================================
    public Reservation findById(Connection conn, Long id) {

        String sql = """
            SELECT * FROM reservations WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Reservation r = new Reservation();

                r.setId(rs.getLong("id"));
                r.setCustomerEmail(rs.getString("customer_email"));
                r.setEventId(rs.getLong("event_id"));
                r.setSeatId(rs.getLong("seat_id"));
                r.setFinalPrice(rs.getDouble("final_price"));
                r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                return r;
            }

        } catch (Exception e) {
            throw new RuntimeException("Find failed", e);
        }

        return null;
    }

    // =====================================================
    // CANCEL
    // =====================================================
    public boolean cancel(Connection conn, Long id) {

        String sql = "UPDATE reservations SET status='CANCELLED' WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Cancel failed", e);
        }
    }

    // =====================================================
    // EXPIRE
    // =====================================================
    public void expire(Connection conn, Long id) {

        String sql = "UPDATE reservations SET status='EXPIRED' WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Expire failed", e);
        }
    }

    // =====================================================
    // FIND EXPIRED HOLDS
    // =====================================================
    public List<Reservation> findExpiredHolds(Connection conn) {

        String sql = """
            SELECT * FROM reservations
            WHERE status='HOLDING'
            AND expires_at < NOW()
        """;

        List<Reservation> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Reservation r = new Reservation();

                r.setId(rs.getLong("id"));
                r.setCustomerEmail(rs.getString("customer_email"));
                r.setEventId(rs.getLong("event_id"));
                r.setSeatId(rs.getLong("seat_id"));
                r.setStatus(ReservationStatus.valueOf(rs.getString("status")));

                Timestamp exp = rs.getTimestamp("expires_at");
                if (exp != null) {
                    r.setExpiresAt(exp.toLocalDateTime());
                }

                list.add(r);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch expired holds", e);
        }

        return list;
    }

    // =====================================================
    // FIND ALL (OPTIONAL)
    // =====================================================
    public List<Reservation> findAll() {

        String sql = "SELECT * FROM reservations";

        List<Reservation> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Reservation r = new Reservation();

                r.setId(rs.getLong("id"));
                r.setCustomerEmail(rs.getString("customer_email"));
                r.setEventId(rs.getLong("event_id"));
                r.setSeatId(rs.getLong("seat_id"));
                r.setFinalPrice(rs.getDouble("final_price"));
                r.setStatus(ReservationStatus.valueOf(rs.getString("status")));

                list.add(r);
            }

        } catch (Exception e) {
            throw new RuntimeException("Fetch failed", e);
        }

        return list;
    }
}