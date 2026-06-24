package service;

import com.ticketing.database.DBConnection;
import com.ticketing.service.ReservationService;
import util.TestDatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.Statement;

public class ReservationDAOTest {

    public static void main(String[] args) {

        ReservationService reservationService =
                new ReservationService();

        reservationService.reserveSeat(
                "john@test.com",
                100L,
                1L
        );

        System.out.println(
                "Reservation completed successfully."
        );
    }
    @BeforeEach
    void setup() {
        TestDatabaseCleaner.cleanAll();

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            // 1. VENUE FIRST
            st.executeUpdate("INSERT INTO venues (id, name) VALUES (1, 'Test Venue')");

            // 2. EVENT (FIX: title not name)
            st.executeUpdate("INSERT INTO events (id, title, venue_id) VALUES (1, 'Test Event', 1)");

            // 3. SEAT
            st.executeUpdate("INSERT INTO seats (id, event_id, type, row_label, seat_number, status) " +
                    "VALUES (1, 1, 'VIP', 'A', 1, 'AVAILABLE')");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}