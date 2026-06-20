package com.ticketing.service;

import com.ticketing.dao.UserDAO;
import com.ticketing.model.User;
import com.ticketing.session.Session;

public class AuthService {

    private final UserDAO userDAO =
            new UserDAO();

    public boolean login(
            String email,
            String password
    ) {

        User user =
                userDAO.findByEmail(email);

        if (user == null) {

            System.out.println(
                    "User not found"
            );

            return false;
        }

        if (!user.getPassword().equals(password)) {

            System.out.println(
                    "Invalid password"
            );

            return false;
        }

        Session.setCurrentUser(user);

        System.out.println(
                "Login successful! Welcome "
                        + user.getName()
        );

        return true;
    }

    public void logout() {

        Session.logout();

        System.out.println(
                "Logged out successfully."
        );
    }
}