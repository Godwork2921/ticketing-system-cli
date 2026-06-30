package util;

import com.ticketing.database.DBConnection;

import java.sql.Connection;

public class TestDataSeeder {

    public static void seed() {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            // CLEAN
            conn.prepareStatement("DELETE FROM reservations").executeUpdate();
            conn.prepareStatement("DELETE FROM seats").executeUpdate();
            conn.prepareStatement("DELETE FROM events").executeUpdate();
            conn.prepareStatement("DELETE FROM venues").executeUpdate();

            // VENUE
            conn.prepareStatement("""
                INSERT INTO venues(id, name, address, timezone)
                VALUES (1, 'Main Hall', 'Addis', 'UTC')
            """).executeUpdate();

            // EVENT
            conn.prepareStatement("""
                INSERT INTO events(id, venue_id, title, start_time, end_time, status)
                VALUES (1, 1, 'Test Event', NOW(), NOW() + interval '2 hour', 'ACTIVE')
            """).executeUpdate();

            // SEAT (IMPORTANT FIX: ensure correct enum string)
            conn.prepareStatement("""
                INSERT INTO seats(id, event_id, section, row_name, seat_number, status)
                VALUES (1, 1, 'A', '1', 1, 'AVAILABLE')
            """).executeUpdate();

            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException("Seeding failed", e);
        }
    }
}