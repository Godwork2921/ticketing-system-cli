package com.ticketing.ui.menu;

import com.ticketing.dao.*;
import com.ticketing.model.*;
import com.ticketing.storage.JsonExporter;
import com.ticketing.storage.JsonImporter;
import com.ticketing.ui.InputScanner;
import com.ticketing.ui.controller.OperatorController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class OperatorMenu {

    private final UserDAO userDAO = new UserDAO();
    private final VenueDAO venueDAO = new VenueDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    private final OperatorController controller = new OperatorController();
    private final JsonImporter importer = new JsonImporter();
    private final JsonExporter exporter = new JsonExporter();

    public void show() {

        while (true) {

            System.out.println("\n========================");
            System.out.println("     OPERATOR MENU");
            System.out.println("========================");

            System.out.println("1. Create Venue");
            System.out.println("2. List Venues");
            System.out.println("3. Create Event");
            System.out.println("4. List Events");
            System.out.println("5. Add Seat To Event");
            System.out.println("6. View Available Seats");
            System.out.println("7. View Reserved Seats");
            System.out.println("8. Export Data To JSON");
            System.out.println("9. Import Data From JSON");
            System.out.println("0. Back");

            System.out.print("Choose: ");
            int choice = InputScanner.nextInt();

            switch (choice) {

                case 1 -> createVenue();
                case 2 -> controller.listVenues();
                case 3 -> createEvent();
                case 4 -> controller.listEvents();
                case 5 -> createSeat();

                case 6 -> {
                    System.out.print("Event ID: ");
                    Long eventId = InputScanner.nextLong();
                    controller.viewAllAvailableSeats(eventId);
                }

                case 7 -> {
                    System.out.print("Event ID: ");
                    Long eventId = InputScanner.nextLong();
                    controller.viewAllReservedSeats(eventId);
                }

                case 8 -> exportData();
                case 9 -> importData();

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // =========================
    // VENUE
    // =========================
    private void createVenue() {

        System.out.print("Venue ID: ");
        Long id = InputScanner.nextLong();

        System.out.print("Venue Name: ");
        String name = InputScanner.nextLine();

        System.out.print("Address: ");
        String address = InputScanner.nextLine();

        System.out.print("Timezone: ");
        String timezone = InputScanner.nextLine();

        controller.createVenue(id, name, address, timezone);
    }

    // =========================
    // EVENT
    // =========================
    private void createEvent() {

        System.out.print("Event ID: ");
        Long id = InputScanner.nextLong();

        System.out.print("Title: ");
        String title = InputScanner.nextLine();

        System.out.print("Venue ID: ");
        Long venueId = InputScanner.nextLong();

        System.out.print("Start (yyyy-MM-ddTHH:mm): ");
        LocalDateTime start = LocalDateTime.parse(InputScanner.nextLine());

        System.out.print("End (yyyy-MM-ddTHH:mm): ");
        LocalDateTime end = LocalDateTime.parse(InputScanner.nextLine());

        controller.createEvent(id, title, venueId, start, end);
    }

    // =========================
    // SEAT
    // =========================
    private void createSeat() {

        System.out.print("Event ID: ");
        Long eventId = InputScanner.nextLong();

        System.out.print("Seat ID: ");
        Long seatId = InputScanner.nextLong();

        System.out.print("Section: ");
        String section = InputScanner.nextLine();

        System.out.print("Row: ");
        String row = InputScanner.nextLine();

        System.out.print("Seat Number: ");
        int number = InputScanner.nextInt();

        controller.createSeat(eventId, seatId, section, row, number);
    }

    // =========================
    // ================= EXPORT =================
    private void exportData() {

        List<User> users = userDAO.findAll();
        List<Venue> venues = venueDAO.findAll();
        List<Event> events = eventDAO.findAll();
        List<Seat> seats = seatDAO.findAll();
        List<Reservation> reservations = reservationDAO.findAll();

        exporter.exportAll(users, venues, events, seats, reservations);

        System.out.println("\n====================================");
        System.out.println("      JSON EXPORT COMPLETED");
        System.out.println("====================================\n");

        printUsers(users);
        printVenues(venues);
        printEvents(events);
        printSeats(seats);
        printReservations(reservations);


        System.out.println("\nFILES GENERATED:");
        System.out.println("users.json");
        System.out.println("venues.json");
        System.out.println("events.json");
        System.out.println("seats.json");
        System.out.println("reservations.json");
    }

    // ================= IMPORT =================
    private void importData() {

        List<User> users = importer.importUsers();
        List<Venue> venues = importer.importVenues();
        List<Event> events = importer.importEvents();
        List<Seat> seats = importer.importSeats();
        List<Reservation> reservations = importer.importReservations();

        System.out.println("\n====================================");
        System.out.println("      JSON IMPORT COMPLETED");
        System.out.println("====================================\n");

        // ================= USERS =================
        System.out.println("📦 USERS LOADED (" + users.size() + ")");
        System.out.println("------------------------------------");

        for (User u : users) {
            System.out.println("✔ ID: " + u.getId()
                    + " | " + u.getName()
                    + " | " + u.getEmail());

            try {
                userDAO.save(u);   // ✅ FIXED (no ID version)
            } catch (Exception ignored) {}
        }

        System.out.println();

        // ================= VENUES =================
        System.out.println("📦 VENUES LOADED (" + venues.size() + ")");
        System.out.println("------------------------------------");

        for (Venue v : venues) {
            System.out.println("✔ ID: " + v.getId()
                    + " | " + v.getName()
                    + " | " + v.getAddress());

            try {
                venueDAO.save(v);
            } catch (Exception ignored) {}
        }

        System.out.println();

        // ================= EVENTS =================
        System.out.println("📦 EVENTS LOADED (" + events.size() + ")");
        System.out.println("------------------------------------");

        for (Event e : events) {
            System.out.println("✔ ID: " + e.getId()
                    + " | " + e.getTitle()
                    + " | Venue: " + e.getVenue().getName());

            try {
                eventDAO.save(e);
            } catch (Exception ignored) {}
        }

        System.out.println();

        // ================= SEATS =================
        System.out.println("📦 SEATS LOADED (" + seats.size() + ")");
        System.out.println("------------------------------------");

        for (Seat s : seats) {
            System.out.println("✔ ID: " + s.getId()
                    + " | Event: " + s.getEventId()
                    + " | " + s.getSection()
                    + " " + s.getRow()
                    + s.getNumber());

            try {
                seatDAO.save(s.getId(), s);   // ✅ FIXED
            } catch (Exception ignored) {}
        }

        System.out.println();

        // ================= RESERVATIONS =================
        System.out.println("📦 RESERVATIONS LOADED (" + reservations.size() + ")");
        System.out.println("------------------------------------");

        // STEP 1: load existing reservations from DB
        Set<String> existingKeys = reservationDAO.findAll()
                .stream()
                .map(Reservation::uniqueKey)
                .collect(java.util.stream.Collectors.toSet());

// STEP 2: import safely (NO duplicates)
        for (Reservation r : reservations) {

            System.out.println(
                    "✔ ID: " + r.getId()
                            + " | User: " + r.getCustomerEmail()
                            + " | Event: " + r.getEventId()
                            + " | Seat: " + r.getSeatId()
                            + " | " + r.getStatus()
            );

            if (!existingKeys.contains(r.uniqueKey())) {
                reservationDAO.save(r);
                existingKeys.add(r.uniqueKey()); // keep updated in memory
            }
        }

        System.out.println("\nDATABASE UPDATED SUCCESSFULLY\n");
    }

    private void printUsers(List<User> users) {

        System.out.println("📦 USERS LOADED (" + users.size() + ")");
        System.out.println("------------------------------------");

        for (User user : users) {
            System.out.println("✔ ID: " + user.getId()
                    + " | " + user.getName()
                    + " | " + user.getEmail());
        }

        System.out.println();
    }

    private void printVenues(List<Venue> venues) {

        System.out.println("📦 VENUES LOADED (" + venues.size() + ")");
        System.out.println("------------------------------------");

        for (Venue venue : venues) {
            System.out.println("✔ ID: " + venue.getId()
                    + " | " + venue.getName()
                    + " | " + venue.getAddress());
        }

        System.out.println();
    }

    private void printEvents(List<Event> events) {

        System.out.println("📦 EVENTS LOADED (" + events.size() + ")");
        System.out.println("------------------------------------");

        for (Event event : events) {
            System.out.println("✔ ID: " + event.getId()
                    + " | " + event.getTitle());
        }

        System.out.println();
    }

    private void printSeats(List<Seat> seats) {

        System.out.println("📦 SEATS LOADED (" + seats.size() + ")");
        System.out.println("------------------------------------");

        for (Seat seat : seats) {
            System.out.println("✔ ID: " + seat.getId()
                    + " | Event " + seat.getEventId()
                    + " | " + seat.getSection()
                    + " " + seat.getRow()
                    + seat.getNumber());
        }

        System.out.println();
    }

    private void printReservations(List<Reservation> reservations) {

        System.out.println("📦 RESERVATIONS LOADED (" + reservations.size() + ")");
        System.out.println("------------------------------------");

        for (Reservation reservation : reservations) {
            System.out.println(
                    "✔ ID: " + reservation.getId()
                            + " | User: " + reservation.getCustomerEmail()
                            + " | Event: " + reservation.getEventId()
                            + " | Seat: " + reservation.getSeatId()
                            + " | " + reservation.getStatus()
            );
        }

        System.out.println();
    }
}
