package com.ticketing.dao;

import com.ticketing.cache.UserCache;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.Role;
import com.ticketing.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // =====================================================
    // SAVE USER
    // =====================================================
    public void save(User user) {

        String sql = """
                INSERT INTO users
                (name, email, password, role)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    // ❌ no setter anymore
                    // we ignore setting id OR reconstruct object if needed
                }
            }

            UserCache.put(user);

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Email already exists!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    // =====================================================
    // FIND BY EMAIL
    // =====================================================
    public User findByEmail(String email) {

        User cached = UserCache.get(email);
        if (cached != null) return cached;

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = mapRow(rs);

                    UserCache.put(user);
                    return user;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to find user", e);
        }

        return null;
    }

    // =====================================================
    // FIND ALL
    // =====================================================
    public List<User> findAll() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load users", e);
        }

        return users;
    }

    // =====================================================
    // MAPPER
    // =====================================================
    private User mapRow(ResultSet rs) throws SQLException {

        return new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                Role.valueOf(rs.getString("role")),
                rs.getString("password")
        );
    }
}