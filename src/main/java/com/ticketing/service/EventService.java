package com.ticketing.service;

import com.ticketing.dao.EventDAO;
import com.ticketing.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventService {

    private final EventDAO eventDAO = new EventDAO();

    // CREATE EVENT
    public void createEvent(Event event) {
        eventDAO.save(event);
    }

    // CHECK DUPLICATE EVENT (title + venue + time overlap)
    public boolean eventExists(String title, Long venueId,
                               LocalDateTime start, LocalDateTime end) {
        return eventDAO.eventExists(title, venueId, start, end);
    }

    // TIME CONFLICT CHECK (venue schedule overlap)
    public boolean hasTimeConflict(Long venueId,
                                   LocalDateTime start,
                                   LocalDateTime end) {

        List<Event> events = eventDAO.findByVenueId(venueId);

        for (Event e : events) {
            if (start.isBefore(e.getEndTime()) &&
                    end.isAfter(e.getStartTime())) {
                return true;
            }
        }

        return false;
    }

    // GET ALL EVENTS
    public List<Event> getAllEvents() {
        return eventDAO.findAll();
    }

    // FIND BY ID
    public Event findById(Long id) {
        return eventDAO.findById(id);
    }
    // DELETE
    public boolean removeEvent(Long id) {
        return eventDAO.delete(id);
    }

    // FIND BY VENUE
    public List<Event> getEventsByVenue(Long venueId) {
        return eventDAO.findByVenueId(venueId);
    }
}