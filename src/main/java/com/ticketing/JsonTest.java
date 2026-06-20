package com.ticketing;

import com.ticketing.dao.*;
import com.ticketing.storage.JsonExporter;

public class JsonTest {

    public static void main(String[] args) {

        JsonExporter exporter = new JsonExporter();

        exporter.exportUsers(
                new UserDAO().findAll()
        );

        exporter.exportVenues(
                new VenueDAO().findAll()
        );

        exporter.exportEvents(
                new EventDAO().findAll()
        );

        exporter.exportSeats(
                new SeatDAO().findAll()
        );

        exporter.exportReservations(
                new ReservationDAO().findAll()
        );

        System.out.println("Export Complete!");
    }
}