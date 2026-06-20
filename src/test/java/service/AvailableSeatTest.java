package service;

import com.ticketing.model.Seat;
import com.ticketing.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ticketing.util.TestDatabaseCleaner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailableSeatTest {

    private final SeatService seatService = new SeatService();

    @BeforeEach
    void setup() {
        TestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldReturnAvailableSeats() {

        List<Seat> seats = seatService.getAvailableSeats(100L);

        assertNotNull(seats);
        assertTrue(seats.isEmpty()); // correct expectation for clean DB
    }
}