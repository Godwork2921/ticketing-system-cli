package com.ticketing.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ticketing.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonImporter {

    public List<User> importUsers() {
        return readFile(
                "users.json",
                new TypeReference<List<User>>() {},
                "Users"
        );
    }

    public List<Venue> importVenues() {
        return readFile(
                "venues.json",
                new TypeReference<List<Venue>>() {},
                "Venues"
        );
    }

    public List<Event> importEvents() {
        return readFile(
                "events.json",
                new TypeReference<List<Event>>() {},
                "Events"
        );
    }

    public List<Seat> importSeats() {
        return readFile(
                "seats.json",
                new TypeReference<List<Seat>>() {},
                "Seats"
        );
    }

    public List<Reservation> importReservations() {
        return readFile(
                "reservations.json",
                new TypeReference<List<Reservation>>() {},
                "Reservations"
        );
    }

    private <T> List<T> readFile(
            String fileName,
            TypeReference<List<T>> typeReference,
            String entityName
    ) {

        try {

            File file = new File(fileName);

            if (!file.exists()) {

                System.out.println(
                        entityName +
                                " file not found: " +
                                file.getAbsolutePath()
                );

                return new ArrayList<>();
            }

            List<T> result =
                    JsonStorageService
                            .getMapper()
                            .readValue(
                                    file,
                                    typeReference
                            );

            System.out.println(
                    entityName +
                            " imported successfully. Count = "
                            + result.size()
            );

            return result;

        } catch (Exception e) {

            System.out.println(
                    "\nFailed to import " +
                            entityName
            );

            e.printStackTrace();

            return new ArrayList<>();
        }
    }
}