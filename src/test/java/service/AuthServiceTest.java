package service;

import com.ticketing.database.DBConnection;
import com.ticketing.service.AuthService;
import util.TestDatabaseCleaner;
import org.mindrot.jbcrypt.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private final AuthService authService =
            new AuthService();

    @BeforeEach
    void setup() {

        TestDatabaseCleaner.cleanAll();

        String hashedSara =
                BCrypt.hashpw("sara123", BCrypt.gensalt());

        String hashedJohn =
                BCrypt.hashpw("john123", BCrypt.gensalt());

        try (
                Connection conn =
                        DBConnection.getConnection();

                Statement st =
                        conn.createStatement()
        ) {

            String sql1 =
                    "INSERT INTO users (id, name, email, password, role) " +
                            "VALUES (1, 'Sara', 'sara@test.com', '" + hashedSara + "', 'CUSTOMER')";

            st.executeUpdate(sql1);

            String sql2 =
                    "INSERT INTO users (id, name, email, password, role) " +
                            "VALUES (2, 'John', 'john@test.com', '" + hashedJohn + "', 'CUSTOMER')";

            st.executeUpdate(sql2);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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