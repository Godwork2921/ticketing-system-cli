package com.ticketing.service;

import com.ticketing.dao.VenueDAO;
import com.ticketing.exception.VenueNotFoundException;
import com.ticketing.model.Venue;

import java.util.List;

public class VenueService {

    private final VenueDAO venueDAO =
            new VenueDAO();

    public void addVenue(Venue venue) {

        venueDAO.save(venue);
    }

    public List<Venue> getAllVenues() {

        return venueDAO.findAll();
    }

    public Venue findVenueById(Long id) {

        Venue venue =
                venueDAO.findById(id);

        if (venue == null) {

            throw new VenueNotFoundException(
                    "Venue not found"
            );
        }

        return venue;
    }

    public boolean removeVenue(Long id) {

        return venueDAO.delete(id);
    }
}