package com.ticketing.dao;

import com.ticketing.database.DBConnection;
import com.ticketing.model.Venue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {

    public void save(Venue venue) {

        String sql = """
                INSERT INTO venues
                (id, name, address, timezone)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setLong(1, venue.getId());
            ps.setString(2, venue.getName());
            ps.setString(3, venue.getAddress());
            ps.setString(4, venue.getTimezone());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save venue",
                    e
            );
        }
    }

    public List<Venue> findAll() {

        List<Venue> venues = new ArrayList<>();

        String sql = "SELECT * FROM venues";

        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                Venue venue = new Venue(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("timezone")
                );

                venues.add(venue);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to load venues",
                    e
            );
        }

        return venues;
    }

    public Venue findById(Long id) {

        String sql =
                "SELECT * FROM venues WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Venue(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("timezone")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find venue with ID: " + id,
                    e
            );
        }

        return null;
    }

    public boolean update(Venue venue) {

        String sql = """
                UPDATE venues
                SET name = ?,
                    address = ?,
                    timezone = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, venue.getName());
            ps.setString(2, venue.getAddress());
            ps.setString(3, venue.getTimezone());
            ps.setLong(4, venue.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to update venue",
                    e
            );
        }
    }

    public boolean delete(Long id) {

        String sql =
                "DELETE FROM venues WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete venue with ID: " + id,
                    e
            );
        }
    }
}