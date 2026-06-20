package service;

import com.ticketing.dao.EventDAO;
import com.ticketing.dao.VenueDAO;
import com.ticketing.enums.EventStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Venue;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventDAOTest {

    public static void main(String[] args) {

        VenueDAO venueDAO = new VenueDAO();

        Venue venue =
                venueDAO.findById(1L);

        EventDAO eventDAO =
                new EventDAO();

        Event event = new Event(
                100L,
                "Java Conference",
                venue,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                EventStatus.ACTIVE,
                new ArrayList<>()
        );

        eventDAO.save(event);

        System.out.println("\nALL EVENTS:");

        eventDAO.findAll()
                .forEach(System.out::println);
    }
}