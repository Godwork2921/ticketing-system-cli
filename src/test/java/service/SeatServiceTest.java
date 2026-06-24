package service;

import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Seat;
import com.ticketing.service.SeatService;
import util.TestDatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeatServiceTest {

    private final SeatDAO seatDAO = new SeatDAO();
    private final SeatService seatService = new SeatService();

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
                    100,
                    'Test Event',
                    1,
                    '2026-01-01 10:00:00',
                    '2026-01-01 12:00:00',
                    'ACTIVE'
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateSeatSuccessfully() {

        Seat seat = new Seat(
                999L,
                100L,
                "VIP",
                "A",
                1,
                SeatStatus.AVAILABLE
        );

        seatService.createSeat(100L, seat);

        Seat saved;

        try (Connection conn = DBConnection.getConnection()) {
            saved = seatDAO.findById(conn, 999L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertNotNull(saved);
        assertEquals(999L, saved.getId());
        assertEquals(100L, saved.getEventId());
        assertEquals(SeatStatus.AVAILABLE, saved.getStatus());
    }

    @Test
    void shouldReturnAvailableSeatsOnly() {

        List<Seat> seats = seatService.getAvailableSeats(100L);

        assertNotNull(seats);

        for (Seat seat : seats) {
            assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        }
    }
}