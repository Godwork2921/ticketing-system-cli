package com.ticketing.util;

import com.ticketing.enums.Role;
import com.ticketing.model.User;

public class DataLoader {

    private DataLoader() {}

    public static void loadSampleUsers() {

        if (!AppContext.userService.emailExists("admin@test.com")) {
            AppContext.userService.registerUser(
                    new User(null, "Admin", "admin@test.com", "admin123", Role.OPERATOR)
            );
        }

        if (!AppContext.userService.emailExists("john@test.com")) {
            AppContext.userService.registerUser(
                    new User(null, "John", "john@test.com", "john123", Role.CUSTOMER)
            );
        }

        if (!AppContext.userService.emailExists("sara@test.com")) {
            AppContext.userService.registerUser(
                    new User(null, "Sara", "sara@test.com", "sara123", Role.CUSTOMER)
            );
        }

        System.out.println("Sample users loaded successfully.");
    }
}