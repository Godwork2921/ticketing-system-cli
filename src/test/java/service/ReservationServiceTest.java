package service;

import com.ticketing.database.DBConnection;
import com.ticketing.model.Reservation;
import com.ticketing.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestDataSeeder;
import util.TestDatabaseCleaner;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {

    private final ReservationService reservationService =
            new ReservationService();

    @BeforeEach
    void setup() {

        TestDatabaseCleaner.cleanAll();
        TestDataSeeder.seed();

        try (
                Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement()
        ) {
            // ✅ Insert with final_price and currency
            st.executeUpdate("""
                INSERT INTO reservations (
                    id,
                    customer_email,
                    event_id,
                    seat_id,
                    status,
                    created_at,
                    final_price,
                    currency
                )
                VALUES (
                    1,
                    'test@mail.com',
                    1,
                    1,
                    'CONFIRMED',
                    NOW(),
                    100.00,
                    'USD'
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldFindReservation() {
        Reservation reservation =
                reservationService.findById(1L);

        assertNotNull(reservation);
    }

    @Test
    void shouldReturnNullForMissingReservation() {
        Reservation reservation =
                reservationService.findById(99999L);

        assertNull(reservation);
    }
}
