package com.ticketing.util;

import com.ticketing.service.EventService;
import com.ticketing.service.ReservationService;
import com.ticketing.service.SeatService;
import com.ticketing.service.UserService;
import com.ticketing.service.VenueService;

public class AppContext {

    public static final UserService userService =
            new UserService();

    public static final EventService eventService =
            new EventService();

    public static final VenueService venueService =
            new VenueService();

    public static final SeatService seatService =
            new SeatService();

    public static final ReservationService reservationService =
            new ReservationService();

    private AppContext() {
    }
}