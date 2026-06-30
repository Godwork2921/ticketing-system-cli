package service;

import com.ticketing.enums.Role;
import com.ticketing.model.User;

public class UserRegistrationTest {

    public static void main(String[] args) {

        User user = new User(
                "John Doe",
                "john@gmail.com",
                "password123",
                Role.OPERATOR
        );

        System.out.println(user);
        System.out.println("User registration test passed");
    }
}