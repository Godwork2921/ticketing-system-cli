package com.ticketing.dao;

import com.ticketing.cache.SeatCache;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    /**
     * Save seat for an event
     */
    public void save(Long eventId, Seat seat) {

        String sql = """
        INSERT INTO seats
        (id, event_id, section, row_name, seat_number, status)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setLong(1, seat.getId());
            ps.setLong(2, eventId);
            ps.setString(3, seat.getSection());
            ps.setString(4, seat.getRow());
            ps.setInt(5, seat.getNumber());
            ps.setString(6, seat.getStatus().name());

            ps.executeUpdate();

            // CACHE UPDATE
            SeatCache.put(seat);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to save seat",
                    e
            );
        }
    }

    public Seat findById(Long id) {

        // CACHE CHECK
        Seat cachedSeat =
                SeatCache.get(id);

        if (cachedSeat != null) {

            System.out.println(
                    "[CACHE HIT] Seat"
            );

            return cachedSeat;
        }

        System.out.println(
                "[CACHE MISS] Seat"
        );

        String sql =
                "SELECT * FROM seats WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                Seat seat =
                        mapRow(rs);

                // STORE IN CACHE
                SeatCache.put(seat);

                return seat;
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * DELETE ALL seats (TESTING ONLY)
     */
    public void deleteAll() {

        String deleteReservations =
                "DELETE FROM reservations";

        String deleteSeats =
                "DELETE FROM seats";

        try (
                Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement()
        ) {

            st.executeUpdate(deleteReservations);
            st.executeUpdate(deleteSeats);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to clear database",
                    e
            );
        }
    }

    public List<Seat> findByEventId(Long eventId) {

        List<Seat> seats =
                new ArrayList<>();

        String sql =
                "SELECT * FROM seats WHERE event_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, eventId);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                seats.add(
                        mapRow(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return seats;
    }

    public List<Seat> findAvailableSeats(Long eventId) {

        String sql = """
        SELECT * FROM seats
        WHERE event_id = ?
        AND status = 'AVAILABLE'
    """;

        List<Seat> seats =
                new ArrayList<>();

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, eventId);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                seats.add(
                        mapRow(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return seats;
    }

    public List<Seat> findAll() {

        List<Seat> seats =
                new ArrayList<>();

        String sql =
                "SELECT * FROM seats";

        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt =
                        conn.createStatement();
                ResultSet rs =
                        stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                seats.add(
                        mapRow(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return seats;
    }

    public boolean existsInEvent(
            Long eventId,
            Long seatId
    ) {

        String sql = """
    SELECT COUNT(*)
    FROM seats
    WHERE event_id = ?
    AND id = ?
    """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, eventId);
            ps.setLong(2, seatId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return false;
    }

    public List<Seat> findReservedSeats(Long eventId) {

        String sql = """
        SELECT * FROM seats
        WHERE event_id = ?
        AND status = 'RESERVED'
    """;

        List<Seat> seats =
                new ArrayList<>();

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, eventId);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                seats.add(
                        mapRow(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return seats;
    }

    private Seat mapRow(ResultSet rs)
            throws SQLException {

        return new Seat(
                rs.getLong("id"),
                rs.getLong("event_id"),
                rs.getString("section"),
                rs.getString("row_name"),
                rs.getInt("seat_number"),
                SeatStatus.valueOf(
                        rs.getString("status")
                )
        );
    }

    public Seat findByIdForUpdate(Long id) {

        String sql = """
        SELECT * FROM seats
        WHERE id = ?
        FOR UPDATE
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // IMPORTANT for locking

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to lock seat", e);
        }

        return null;
    }

    public boolean updateStatus(
            Long seatId,
            SeatStatus status
    ) {

        String sql =
                "UPDATE seats SET status = ? WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    status.name()
            );

            ps.setLong(
                    2,
                    seatId
            );

            if (ps.executeUpdate() > 0) {

                Seat seat =
                        findById(seatId);

                if (seat != null) {

                    seat.setStatus(status);

                    SeatCache.put(seat);
                }

                return true;
            }

            return false;

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }
}
