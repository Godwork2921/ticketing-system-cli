package com.ticketing.dao;

import com.ticketing.cache.EventCache;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.EventStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Venue;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private final VenueDAO venueDAO =
            new VenueDAO();

    public void save(Event event) {

        String sql = """
            INSERT INTO events
            (id, title, venue_id, start_time, end_time, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, event.getId());
            ps.setString(2, event.getTitle());
            ps.setLong(3, event.getVenue().getId());
            ps.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            event.getStartTime()
                    )
            );
            ps.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            event.getEndTime()
                    )
            );
            ps.setString(
                    6,
                    event.getStatus().name()
            );

            ps.executeUpdate();

            // CACHE UPDATE
            EventCache.put(event);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to save event",
                    e
            );
        }
    }

    public List<Event> findAll() {

        List<Event> events =
                new ArrayList<>();

        String sql =
                "SELECT * FROM events";

        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt =
                        conn.createStatement();
                ResultSet rs =
                        stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                Long venueId =
                        rs.getLong("venue_id");

                Venue venue =
                        venueDAO.findById(
                                venueId
                        );

                Event event =
                        new Event(
                                rs.getLong("id"),
                                rs.getString("title"),
                                venue,
                                rs.getTimestamp("start_time")
                                        .toLocalDateTime(),
                                rs.getTimestamp("end_time")
                                        .toLocalDateTime(),
                                EventStatus.valueOf(
                                        rs.getString("status")
                                ),
                                new ArrayList<>()
                        );

                events.add(event);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to load events",
                    e
            );
        }

        return events;
    }

    public Event findById(Long id) {

        // CACHE CHECK
        Event cachedEvent =
                EventCache.get(id);

        if (cachedEvent != null) {

            System.out.println(
                    "[CACHE HIT] Event"
            );

            return cachedEvent;
        }

        System.out.println(
                "[CACHE MISS] Event"
        );

        String sql =
                "SELECT * FROM events WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Long venueId =
                            rs.getLong("venue_id");

                    Venue venue =
                            venueDAO.findById(
                                    venueId
                            );

                    Event event =
                            new Event(
                                    rs.getLong("id"),
                                    rs.getString("title"),
                                    venue,
                                    rs.getTimestamp("start_time")
                                            .toLocalDateTime(),
                                    rs.getTimestamp("end_time")
                                            .toLocalDateTime(),
                                    EventStatus.valueOf(
                                            rs.getString("status")
                                    ),
                                    new ArrayList<>()
                            );

                    // STORE IN CACHE
                    EventCache.put(event);

                    return event;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find event with ID: " + id,
                    e
            );
        }

        return null;
    }

    public List<Event> findByVenueId(Long venueId) {

        List<Event> events =
                new ArrayList<>();

        String sql =
                "SELECT * FROM events WHERE venue_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, venueId);

            try (ResultSet rs = ps.executeQuery()) {

                Venue venue =
                        venueDAO.findById(
                                venueId
                        );

                while (rs.next()) {

                    Event event =
                            new Event(
                                    rs.getLong("id"),
                                    rs.getString("title"),
                                    venue,
                                    rs.getTimestamp("start_time")
                                            .toLocalDateTime(),
                                    rs.getTimestamp("end_time")
                                            .toLocalDateTime(),
                                    EventStatus.valueOf(
                                            rs.getString("status")
                                    ),
                                    new ArrayList<>()
                            );

                    events.add(event);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to load events for venue",
                    e
            );
        }

        return events;
    }

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
    AND (
            (? < end_time)
        AND (? > start_time)
    )
    """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, title);
            ps.setLong(2, venueId);
            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(start)
            );
            ps.setTimestamp(
                    4,
                    Timestamp.valueOf(end)
            );

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean update(Event event) {

        String sql = """
            UPDATE events
            SET title = ?,
                venue_id = ?,
                start_time = ?,
                end_time = ?,
                status = ?
            WHERE id = ?
            """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, event.getTitle());
            ps.setLong(2, event.getVenue().getId());
            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            event.getStartTime()
                    )
            );
            ps.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            event.getEndTime()
                    )
            );
            ps.setString(
                    5,
                    event.getStatus().name()
            );
            ps.setLong(
                    6,
                    event.getId()
            );

            if (ps.executeUpdate() > 0) {

                EventCache.put(event);

                return true;
            }

            return false;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to update event",
                    e
            );
        }
    }

    public boolean delete(Long id) {

        String sql =
                "DELETE FROM events WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            if (ps.executeUpdate() > 0) {

                EventCache.remove(id);

                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete event",
                    e
            );
        }
    }
}