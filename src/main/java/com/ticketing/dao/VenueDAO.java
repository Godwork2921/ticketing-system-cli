package com.ticketing.dao;

import com.ticketing.cache.VenueCache;
import com.ticketing.database.DBConnection;
import com.ticketing.model.Venue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {

    // =====================================================
    // SAVE
    // =====================================================
    public void save(Venue venue) {

        String sql = """
            INSERT INTO venues
            (name, address, timezone)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, venue.getName());
            ps.setString(2, venue.getAddress());
            ps.setString(3, venue.getTimezone());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    // no setter → ID should be set via constructor design
                    long id = keys.getLong(1);
                    // optional: recreate object or ignore in domain
                }
            }

            VenueCache.put(venue);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save venue", e);
        }
    }

    // =====================================================
    // FIND ALL
    // =====================================================
    public List<Venue> findAll() {

        List<Venue> venues = new ArrayList<>();

        String sql = "SELECT * FROM venues";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                venues.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load venues", e);
        }

        return venues;
    }

    // =====================================================
    // FIND BY ID (WITH CACHE)
    // =====================================================
    public Venue findById(Long id) {

        Venue cached = VenueCache.get(id);
        if (cached != null) {
            return cached;
        }

        String sql = "SELECT * FROM venues WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Venue venue = mapRow(rs);
                    VenueCache.put(venue);
                    return venue;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find venue", e);
        }

        return null;
    }

    // =====================================================
    // UPDATE
    // =====================================================
    public boolean update(Venue venue) {

        String sql = """
            UPDATE venues
            SET name = ?,
                address = ?,
                timezone = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, venue.getName());
            ps.setString(2, venue.getAddress());
            ps.setString(3, venue.getTimezone());
            ps.setLong(4, venue.getId());

            int updated = ps.executeUpdate();

            if (updated > 0) {
                VenueCache.put(venue);
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update venue", e);
        }
    }

    // =====================================================
    // DELETE
    // =====================================================
    public boolean delete(Long id) {

        String sql = "DELETE FROM venues WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int deleted = ps.executeUpdate();

            if (deleted > 0) {
                VenueCache.remove(id);
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete venue", e);
        }
    }

    // =====================================================
    // MAPPER (IMPORTANT CLEAN DESIGN)
    // =====================================================
    private Venue mapRow(ResultSet rs) throws SQLException {

        return new Venue(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("timezone")
        );
    }
}