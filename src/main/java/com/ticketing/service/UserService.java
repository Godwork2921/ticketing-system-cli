package com.ticketing.service;

import com.ticketing.dao.UserDAO;
import com.ticketing.model.User;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Register a new user (DB-based)
     */
    public void registerUser(User user) {

        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        // check duplicate email
        User existing = userDAO.findByEmail(user.getEmail());

        if (existing != null) {
            throw new RuntimeException("Email already registered");
        }

        userDAO.save(user);
    }

    /**
     * Find user by email (used for login)
     */
    public User findByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email cannot be empty");
        }

        return userDAO.findByEmail(email);
    }

    /**
     * Get all users from DB
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        return userDAO.findByEmail(email) != null;
    }

    /**
     * Validate login credentials (optional helper)
     */
    public User validateLogin(String email, String password) {

        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }
}