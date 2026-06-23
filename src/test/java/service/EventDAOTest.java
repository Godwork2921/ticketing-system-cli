package service;

import com.ticketing.enums.EventStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Venue;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventDAOTest {

    public static void main(String[] args) {

        Venue venue = new Venue();
        venue.setId(1L);
        venue.setName("Test Venue");

        Event event = new Event(
                1L,
                "Concert",
                venue,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                100.0, // ✅ FIXED: basePrice added
                EventStatus.ACTIVE,
                new ArrayList<>()
        );

        System.out.println(event);
        System.out.println("Event test passed");
    }
}