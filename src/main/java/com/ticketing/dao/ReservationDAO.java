package com.ticketing.dao;

import com.ticketing.database.DBConnection;
import com.ticketing.enums.ReservationStatus;
import com.ticketing.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // SAVE (AUTO GENERATED ID)
    public void save(Reservation reservation) {

        String sql = """
        INSERT INTO reservations
        (customer_email, event_id, seat_id, status, created_at)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reservation.getCustomerEmail());
            ps.setLong(2, reservation.getEventId());
            ps.setLong(3, reservation.getSeatId());
            ps.setString(4, reservation.getStatus().name());
            ps.setTimestamp(5, Timestamp.valueOf(reservation.getCreatedAt()));

            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            // 🔥 duplicate ignored safely
            System.out.println("Duplicate reservation ignored.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to save reservation", e);
        }
    }
    // CHECK DOUBLE BOOKING
    public boolean existsBySeatId(Long seatId) {

        String sql = """
            SELECT COUNT(*) 
            FROM reservations 
            WHERE seat_id = ? AND status = 'CONFIRMED'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, seatId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    // FIND ALL
    public List<Reservation> findAll() {

        List<Reservation> list = new ArrayList<>();

        String sql = "SELECT * FROM reservations";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // FIND BY ID
    public Reservation findById(Long id) {

        String sql = "SELECT * FROM reservations WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // CANCEL
    public boolean cancel(
            Long reservationId
    ) {

        String sql = """
            UPDATE reservations
            SET status = 'CANCELLED'
            WHERE id = ?
            """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    reservationId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to cancel reservation",
                    e
            );
        }
    }

    // MAPPER
    private Reservation mapRow(ResultSet rs) throws SQLException {

        Reservation r = new Reservation();

        r.setId(rs.getLong("id"));
        r.setCustomerEmail(rs.getString("customer_email"));
        r.setEventId(rs.getLong("event_id"));
        r.setSeatId(rs.getLong("seat_id"));
        r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return r;
    }
}