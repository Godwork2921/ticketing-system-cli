package com.ticketing.ui.controller;

import com.ticketing.enums.Role;
import com.ticketing.model.User;
import com.ticketing.service.UserService;
import com.ticketing.session.Session;

public class LoginController {

    private final UserService userService = new UserService();

    public User login(String email, String password) {

        User user = userService.findByEmail(email);

        if (user == null) return null;

        if (!user.getPassword().equals(password)) return null;

        Session.setCurrentUser(user);

        return user;
    }

    public void registerCustomer(
            Long id,
            String name,
            String email,
            String password
    ) {

        // ✅ FIXED ORDER: role BEFORE password
        User user = new User(
                id,
                name,
                email,
                Role.CUSTOMER,
                password
        );

        userService.registerUser(user);
    }
}