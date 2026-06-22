package com.ticketing.service;

import com.ticketing.dao.EventDAO;
import com.ticketing.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventService {

    private final EventDAO eventDAO =
            new EventDAO();

    public void createEvent(Event event) {

        eventDAO.save(event);
    }


    public boolean eventExists(
            String title,
            Long venueId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return eventDAO.eventExists(
                title,
                venueId,
                start,
                end
        );
    }


    public boolean hasConflict(Long venueId,
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


    public boolean hasTimeConflict(
            Long venueId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        List<Event> events = eventDAO.findAll();

        for (Event event : events) {

            if (!event.getVenue().getId().equals(venueId)) {
                continue;
            }

            boolean overlap =
                    start.isBefore(event.getEndTime())
                            &&
                            end.isAfter(event.getStartTime());

            if (overlap) {
                return true;
            }
        }

        return false;
    }
    public List<Event> getAllEvents() {

        return eventDAO.findAll();
    }

    public Event findById(Long id) {

        return eventDAO.findById(id);
    }

    public boolean removeEvent(Long id) {

        return eventDAO.delete(id);
    }

    public List<Event> getEventsByVenue(Long venueId) {

        return eventDAO.findByVenueId(venueId);
    }
}