package com.ticketing.factory;

import com.ticketing.enums.Role;
import com.ticketing.model.User;

public class UserFactory {

    private UserFactory() {}

    public static User createCustomer(
            Long id,
            String name,
            String email,
            String password
    ) {

        return new User(
                id,
                name,
                email,
                Role.CUSTOMER,
                password
        );
    }

    public static User createOperator(
            Long id,
            String name,
            String email,
            String password
    ) {

        return new User(
                id,
                name,
                email,
                Role.OPERATOR,
                password
        );
    }
}