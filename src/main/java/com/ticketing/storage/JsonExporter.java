package com.ticketing.storage;

import com.ticketing.model.*;

import java.io.File;
import java.util.List;

public class JsonExporter {

    private static final String USERS_FILE = "users.json";
    private static final String VENUES_FILE = "venues.json";
    private static final String EVENTS_FILE = "events.json";
    private static final String SEATS_FILE = "seats.json";
    private static final String RESERVATIONS_FILE = "reservations.json";

    public void exportUsers(List<User> users) {
        writeToFile(USERS_FILE, users, "Users");
    }

    public void exportVenues(List<Venue> venues) {
        writeToFile(VENUES_FILE, venues, "Venues");
    }

    public void exportEvents(List<Event> events) {
        writeToFile(EVENTS_FILE, events, "Events");
    }

    public void exportSeats(List<Seat> seats) {
        writeToFile(SEATS_FILE, seats, "Seats");
    }

    public void exportReservations(List<Reservation> reservations) {
        writeToFile(RESERVATIONS_FILE, reservations, "Reservations");
    }

    private void writeToFile(
            String fileName,
            Object data,
            String entityName) {

        try {

            File file = new File(fileName);

            JsonStorageService
                    .getMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, data);

            System.out.println(
                    entityName +
                            " exported successfully."
            );

            System.out.println(
                    "Saved to: " +
                            file.getAbsolutePath()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export " + entityName,
                    e
            );
        }
    }

    public void exportAll(
            List<User> users,
            List<Venue> venues,
            List<Event> events,
            List<Seat> seats,
            List<Reservation> reservations
    ) {

        exportUsers(users);
        exportVenues(venues);
        exportEvents(events);
        exportSeats(seats);
        exportReservations(reservations);

        System.out.println(
                "\nAll data exported successfully."
        );
    }
}