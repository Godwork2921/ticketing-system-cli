package service;

import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Seat;
import com.ticketing.util.TestDatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class SeatDAOTest {

    private final SeatDAO dao = new SeatDAO();

    @BeforeEach
    void setup() {
        TestDatabaseCleaner.cleanAll();

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            // 1. VENUE FIRST
            st.executeUpdate("""
            INSERT INTO venues (id, name, address, timezone)
            VALUES (1, 'Test Venue', 'Addis', 'UTC')
        """);

            // 2. EVENT SECOND (must include ALL NOT NULL columns)
            st.executeUpdate("""
            INSERT INTO events (
                id, title, venue_id,
                start_time, end_time, status
            )
            VALUES (
                1,
                'Test Event',
                1,
                NOW(),
                NOW(),
                'ACTIVE'
            )
        """);

            // 3. SEAT LAST
            st.executeUpdate("""
            INSERT INTO seats (
                id, event_id, section, row_name,
                seat_number, status
            )
            VALUES (
                1,
                1,
                'VIP',
                'A',
                1,
                'AVAILABLE'
            )
        """);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSaveAndRetrieveSeat() {

        long eventId = 1000L;
        long seatId = System.currentTimeMillis(); // unique

        Seat seat = new Seat(
                seatId,
                eventId,
                "VIP",
                "A",
                1,
                SeatStatus.AVAILABLE
        );;

        dao.save(1L, seat);

        Seat result = dao.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getEventId());
        assertEquals(SeatStatus.AVAILABLE, result.getStatus());
    }
}