package service;

import com.ticketing.model.Reservation;
import com.ticketing.service.ReservationService;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;
import com.ticketing.util.TestDatabaseCleaner;
import com.ticketing.database.DBConnection;
import org.junit.jupiter.api.BeforeEach;

public class ReservationServiceTest {

    private final ReservationService reservationService =
            new ReservationService();

    @Test
    void shouldFindReservation() {

        Reservation reservation =
                reservationService.findById(1L);

        assertNotNull(reservation);
    }

    @BeforeEach
    void setup() {
        TestDatabaseCleaner.cleanAll();

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate("""
            INSERT INTO venues (id, name, address, timezone)
            VALUES (1, 'Test Venue', 'Addis', 'UTC')
        """);

            st.executeUpdate("""
            INSERT INTO events (
                id, title, venue_id,
                start_time, end_time, status
            )
            VALUES (
                1,
                'Test Event',
                1,
                '2026-01-01 10:00:00',
                '2026-01-01 12:00:00',
                'ACTIVE'
            )
        """);

            st.executeUpdate("""
            INSERT INTO seats (
                id, event_id, section, row_name,
                seat_number, status
            )
            VALUES (
                1,
                    
                1,
                'A',
                'Row1',
                10,
                'AVAILABLE'
            )
        """);

            st.executeUpdate("""
            INSERT INTO reservations (
                id, customer_email, event_id, seat_id, status, created_at
            )
            VALUES (
                1,
                'test@mail.com',
                1,
                1,
                'CONFIRMED',
                NOW()
            )
        """);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void shouldReturnNullForMissingReservation() {

        Reservation reservation =
                reservationService.findById(99999L);

        assertNull(reservation);
    }
}