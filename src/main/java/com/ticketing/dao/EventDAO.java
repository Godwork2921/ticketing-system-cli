package com.ticketing.dao;

import com.ticketing.database.DBConnection;
import com.ticketing.enums.EventStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Venue;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private final VenueDAO venueDAO = new VenueDAO();

    // ================= SAVE =================
    public void save(Event event) {

        String sql = """
            INSERT INTO events
            (id, title, venue_id, base_price, start_time, end_time, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, event.getId());
            ps.setString(2, event.getTitle());
            ps.setLong(3, event.getVenue().getId());
            ps.setDouble(4, event.getBasePrice());
            ps.setTimestamp(5, Timestamp.valueOf(event.getStartTime()));
            ps.setTimestamp(6, Timestamp.valueOf(event.getEndTime()));
            ps.setString(7, event.getStatus().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save event", e);
        }
    }

    // ================= FIND ALL =================
    public List<Event> findAll() {

        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events", e);
        }

        return events;
    }

    // ================= FIND BY ID =================
    public Event findById(Long id) {

        String sql = "SELECT * FROM events WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find event", e);
        }

        return null;
    }

    // ================= FIND BY VENUE =================
    public List<Event> findByVenueId(Long venueId) {

        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events WHERE venue_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, venueId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    events.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events by venue", e);
        }

        return events;
    }

    // ================= EVENT EXISTS (CONFLICT CHECK) =================
    public boolean eventExists(
            String title,
            Long venueId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        String sql = """
            SELECT COUNT(*)
            FROM events
            WHERE LOWER(title) = LOWER(?)
            AND venue_id = ?
            AND (? < end_time AND ? > start_time)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setLong(2, venueId);
            ps.setTimestamp(3, Timestamp.valueOf(start));
            ps.setTimestamp(4, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check event existence", e);
        }

        return false;
    }

    // ================= UPDATE =================
    public boolean update(Event event) {

        String sql = """
            UPDATE events
            SET title = ?, venue_id = ?, base_price = ?,
                start_time = ?, end_time = ?, status = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getTitle());
            ps.setLong(2, event.getVenue().getId());
            ps.setDouble(3, event.getBasePrice());
            ps.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            ps.setString(6, event.getStatus().name());
            ps.setLong(7, event.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update event", e);
        }
    }

    // ================= DELETE =================
    public boolean delete(Long id) {

        String sql = "DELETE FROM events WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete event", e);
        }
    }

    // ================= MAPPER (FIXED + CLEAN) =================
    private Event mapRow(ResultSet rs) throws SQLException {

        Venue venue = venueDAO.findById(rs.getLong("venue_id"));

        return new Event(
                rs.getLong("id"),
                rs.getString("title"),
                venue,
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime(),
                rs.getDouble("base_price"),
                EventStatus.valueOf(rs.getString("status")),
                new ArrayList<>()
        );
    }
}