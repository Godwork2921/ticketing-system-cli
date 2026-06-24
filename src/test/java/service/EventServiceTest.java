package service;

import com.ticketing.model.Event;
import com.ticketing.service.EventService;
import org.junit.jupiter.api.Test;
import com.ticketing.database.DBConnection;
import util.TestDatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {

    private final EventService eventService =
            new EventService();

    @Test
    void shouldFindEventById() {

        Event event =
                eventService.findById(1L);

        assertNotNull(event);
    }

    @BeforeEach
    void setup() {

        TestDatabaseCleaner.cleanAll();

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate("INSERT INTO venues (id, name) VALUES (1, 'Test Venue')");

            st.executeUpdate("""
            INSERT INTO events (id, title, venue_id, start_time, end_time, status)
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
    void shouldReturnNullForInvalidEvent() {
        Event event = eventService.findById(99999L);
        assertNull(event);
    }
}