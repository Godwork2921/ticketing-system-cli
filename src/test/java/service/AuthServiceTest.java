package service;

import com.ticketing.service.AuthService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private final AuthService authService =
            new AuthService();

    @Test
    void shouldLoginWithValidCredentials() {

        boolean result =
                authService.login(
                        "sara@test.com",
                        "sara123"
                );

        assertTrue(result);
    }

    @Test
    void shouldFailWithWrongPassword() {

        boolean result =
                authService.login(
                        "john@test.com",
                        "wrongPassword"
                );

        assertFalse(result);
    }
}