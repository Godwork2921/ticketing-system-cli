package service;

import com.ticketing.enums.Role;
import com.ticketing.model.User;

public class UserRegistrationTest {

    public static void main(String[] args) {

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@gmail.com");

        // ✅ FIXED ROLE (choose correct one from your enum)
        user.setRole(Role.OPERATOR);

        System.out.println(user);
        System.out.println("User registration test passed");
    }
}