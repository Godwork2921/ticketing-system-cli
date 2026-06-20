package com.ticketing.ui.controller;

import com.ticketing.model.User;
import com.ticketing.util.AppContext;

public class AuthController {

    public User login(String email) {

        User user = AppContext.userService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Invalid email");
        }

        return user;
    }
}