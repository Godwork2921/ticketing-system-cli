package com.ticketing.repository;

import com.ticketing.database.DBConnection;
import com.ticketing.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    public void save(Event event) {

        String sql = "INSERT INTO event VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, event.getId());
            ps.setString(2, event.getTitle());
            ps.setLong(3, event.getVenue().getId());
            ps.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
            ps.setString(6, event.getStatus().name());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Event> findAll() {
        return new ArrayList<>();
    }
}