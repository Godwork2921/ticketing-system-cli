package com.ticketing.service;

import com.ticketing.dao.EventDAO;
import com.ticketing.model.Event;

import java.util.List;

public class EventService {

    private final EventDAO eventDAO =
            new EventDAO();

    public void createEvent(Event event) {

        eventDAO.save(event);
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