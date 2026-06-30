package com.ticketing.service;

import com.ticketing.dao.UserDAO;
import com.ticketing.enums.Role;
import com.ticketing.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // =====================================================
    // REGISTER USER
    // =====================================================
    public void registerUser(User user) {

        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        // DO NOT mutate domain object → create new secure instance
        User secureUser = new User(
                user.getName(),
                user.getEmail(),
                BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()),
                user.getRole() == null ? Role.CUSTOMER : user.getRole()
        );

        userDAO.save(secureUser);
    }

    // =====================================================
    // FIND USER
    // =====================================================
    public User findByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email cannot be empty");
        }

        return userDAO.findByEmail(email);
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    // =====================================================
    // CHECK EMAIL EXISTS
    // =====================================================
    public boolean emailExists(String email) {
        return userDAO.findByEmail(email) != null;
    }

    // =====================================================
    // LOGIN VALIDATION
    // =====================================================
    public User validateLogin(String email, String password) {

        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            return null;
        }

        return user;
    }
}