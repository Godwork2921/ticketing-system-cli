package com.ticketing.dao;

import com.ticketing.database.DBConnection;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.model.Money;
import com.ticketing.model.Reservation;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ReservationDAO {

    // =====================================================
    // SAVE
    // =====================================================
    public void save(Connection conn, Reservation r) {
        String sql = """
            INSERT INTO reservations
            (customer_email, event_id, seat_id, status, created_at, final_price, currency, expires_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getCustomerEmail());
            ps.setLong(2, r.getEventId());
            ps.setLong(3, r.getSeatId());
            ps.setString(4, r.getStatus().name());
            ps.setTimestamp(5, Timestamp.valueOf(r.getCreatedAt()));

            Money price = r.getFinalPrice();
            if (price != null) {
                ps.setBigDecimal(6, price.amount());
                ps.setString(7, price.currency().getCurrencyCode());
            } else {
                ps.setNull(6, Types.DECIMAL);
                ps.setNull(7, Types.VARCHAR);
            }

            if (r.getExpiresAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(r.getExpiresAt()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

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
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
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
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch expired holds", e);
        }
        return list;
    }

    // =====================================================
    // FIND ALL
    // =====================================================
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Fetch failed", e);
        }
        return list;
    }

    // =====================================================
    // MAP ROW
    // =====================================================
    private Reservation mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String customerEmail = rs.getString("customer_email");
        Long eventId = rs.getLong("event_id");
        Long seatId = rs.getLong("seat_id");
        ReservationStatus status = ReservationStatus.valueOf(rs.getString("status"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        // ✅ Read Money fields
        BigDecimal amount = rs.getBigDecimal("final_price");
        String currencyCode = rs.getString("currency");
        Money price = null;
        if (amount != null && currencyCode != null) {
            price = new Money(amount.setScale(2, java.math.RoundingMode.HALF_UP),
                    Currency.getInstance(currencyCode));
        }

        LocalDateTime expiresAt = null;
        Timestamp expiresTs = rs.getTimestamp("expires_at");
        if (expiresTs != null) {
            expiresAt = expiresTs.toLocalDateTime();
        }

        return new Reservation(id, customerEmail, eventId, seatId,
                status, createdAt, price, expiresAt);
    }
}
