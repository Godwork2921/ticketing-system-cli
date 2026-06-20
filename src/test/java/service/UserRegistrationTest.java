package service;

import com.ticketing.dao.UserDAO;
import com.ticketing.enums.Role;
import com.ticketing.model.User;
import com.ticketing.service.UserService;
import com.ticketing.util.TestDatabaseCleaner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationTest {

    private final UserService userService =
            new UserService();

    private final UserDAO userDAO =
            new UserDAO();

    @BeforeEach
    void setup() {
        TestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {

        User user =
                new User(
                        null,
                        "Abebe",
                        "abebe@test.com",
                        "abebe123",
                        Role.CUSTOMER
                );

        userService.registerUser(user);

        User saved =
                userDAO.findByEmail(
                        "abebe@test.com"
                );

        assertNotNull(saved);

        assertEquals(
                "Abebe",
                saved.getName()
        );

        assertEquals(
                "abebe@test.com",
                saved.getEmail()
        );
    }

    @Test
    void shouldRejectDuplicateEmail() {

        User user1 = new User(
                null,
                "John",
                "john@test.com",
                "john123",
                Role.CUSTOMER
        );

        userService.registerUser(user1);

        User user2 = new User(
                null,
                "Another John",
                "john@test.com",
                "password123",
                Role.CUSTOMER
        );

        assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(user2)
        );

    }
}