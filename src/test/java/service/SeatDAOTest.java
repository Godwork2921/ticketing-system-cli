package service;

import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Seat;
import util.TestDatabaseCleaner;
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
                    NOW(),
                    NOW(),
                    'ACTIVE'
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSaveAndRetrieveSeat() {

        long seatId = 999L;

        Seat seat = new Seat(
                seatId,
                1L,
                "VIP",
                "A",
                1,
                SeatStatus.AVAILABLE
        );

        // SAVE
        dao.save(1L, seat);

        Seat result;

        try (Connection conn = DBConnection.getConnection()) {
            result = dao.findById(conn, seatId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertNotNull(result, "Seat should not be null");
        assertEquals(seatId, result.getId());
        assertEquals(1L, result.getEventId());
        assertEquals(SeatStatus.AVAILABLE, result.getStatus());
    }
}