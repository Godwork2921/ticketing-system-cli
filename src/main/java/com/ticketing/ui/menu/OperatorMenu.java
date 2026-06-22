package com.ticketing.ui.menu;
import java.time.ZoneId;
import com.ticketing.dao.*;
import com.ticketing.model.*;
import com.ticketing.service.EventService;
import com.ticketing.service.SeatService;
import com.ticketing.service.VenueService;
import com.ticketing.storage.JsonExporter;
import com.ticketing.storage.JsonImporter;
import com.ticketing.ui.InputScanner;
import com.ticketing.ui.controller.OperatorController;
import com.ticketing.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class OperatorMenu {
    private final VenueService venueService = new VenueService();
    private final UserDAO userDAO = new UserDAO();
    private final VenueDAO venueDAO = new VenueDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final EventService eventService = new EventService();
    private final OperatorController controller = new OperatorController();
    private final JsonImporter importer = new JsonImporter();
    private final JsonExporter exporter = new JsonExporter();
    private final SeatService seatService = new SeatService();

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

        Long id;

        while (true) {

            try {

                System.out.print("Venue ID: ");

                id = InputScanner.nextLong();

                if (!ValidationUtil.isValidId(id)) {
                    System.out.println("Invalid ID! Must be number > 0");
                    continue;
                }

                if (venueDAO.findById(id) != null) {

                    System.out.println(
                            "This Venue ID already exists! Please enter a new Venue ID."
                    );

                    continue;
                }

                break;

            } catch (Exception e) {

                System.out.println(
                        "Enter a valid long number."
                );

                InputScanner.nextLine();
            }
        }

        String name;

        while (true) {

            System.out.print("Venue Name: ");
               name = InputScanner.nextLine();

            if (ValidationUtil.isValidName(name)) {
                break;
            }


            System.out.println(
                    "Invalid Venue Name! Letters and spaces only."
            );
        }

        String address;
        while (true) {
            System.out.print("Address: ");
            address = InputScanner.nextLine();

            if (ValidationUtil.isValidAddress(address)) {
                break;
            }
            System.out.println(
                    "Invalid Venue address! Letters or street number only."
            );
        }


        String timezone;

        while (true) {

            System.out.print("Timezone: ");
            timezone = InputScanner.nextLine();

            if (ValidationUtil.isValidTimezone(timezone)) {
                break;
            }

            System.out.println(
                    "Invalid timezone! Use a valid ZoneId such as Africa/Addis_Ababa"
            );
        }




        controller.createVenue(id, name, address, timezone);
    }

    public Venue findById(Long id) {
        return venueDAO.findById(id);
    }
    // =========================
    // EVENT
    // =========================
    private void createEvent() {

        Long eventId;

        // =========================
        // EVENT ID
        // =========================
        while (true) {

            try {
                System.out.print("Event ID: ");
                eventId = InputScanner.nextLong();

                if (!ValidationUtil.isValidId(eventId)) {
                    System.out.println("Invalid ID! Must be number > 0");
                    continue;
                }

                if (eventService.findById(eventId) != null) {
                    System.out.println("This Event ID already exists!");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Enter a valid long number.");
                InputScanner.nextLine();
            }
        }



        // =========================
        // TITLE
        // =========================
        String title;

        while (true) {

            System.out.print("Title: ");
            title = InputScanner.nextLine();

            if (ValidationUtil.isValidEventTitle(title)) {
                break;
            }

            System.out.println("Invalid title! Letters, numbers, spaces and '-' only.");
        }

        // =========================
        // VENUE ID
        // =========================
        Long venueId;

        while (true) {

            try {
                System.out.print("Venue ID: ");
                venueId = InputScanner.nextLong();

                if (!ValidationUtil.isValidId(venueId)) {
                    System.out.println("Invalid venue ID!");
                    continue;
                }

                if (venueService.findVenueById(venueId) == null) {
                    System.out.println("Venue not found!");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Invalid Venue ID!");
                InputScanner.nextLine();
            }
        }



        // =========================
        // START TIME
        // =========================
        LocalDateTime start;

        while (true) {

            try {
                System.out.print("Start (yyyy-MM-ddTHH:mm): ");
                start = LocalDateTime.parse(InputScanner.nextLine());
                break;

            } catch (Exception e) {
                System.out.println("Invalid date format!");
            }
        }

        // =========================
        // END TIME
        // =========================
        LocalDateTime end;

        while (true) {

            try {
                System.out.print("End (yyyy-MM-ddTHH:mm): ");
                end = LocalDateTime.parse(InputScanner.nextLine());

                if (end.isAfter(start)) {
                    break;
                }

                System.out.println("End time must be after start time!");

            } catch (Exception e) {
                System.out.println("Invalid date format!");
            }
        }

        // =========================
        // CONFLICT CHECK (TIME + VENUE)
        // =========================
        if (eventService.hasTimeConflict(venueId, start, end)) {
            System.out.println("Another event already exists at this venue during that time!");
            return;
        }

        // =========================
        // DUPLICATE EVENT CHECK
        // =========================
        if (eventService.eventExists(title, venueId, start, end)) {
            System.out.println("Event with same title already exists at this venue & time!");
            return;
        }

        // =========================
        // CREATE EVENT
        // =========================
        controller.createEvent(eventId, title, venueId, start, end);

        System.out.println("Event created successfully!");
    }
    // =========================
    // SEAT
    // =========================
    private void createSeat() {

        Long eventId;

        while (true) {

            try {

                System.out.print("Event ID: ");

                eventId = InputScanner.nextLong();

                if (eventService.findById(eventId) != null) {
                    break;
                }

                System.out.println("Event not found!");

            } catch (Exception e) {

                System.out.println("Invalid Event ID!");
                InputScanner.nextLine();
            }
        }

        Long seatId;

        while (true) {

            try {

                System.out.print("Seat ID: ");

                seatId = InputScanner.nextLong();

                if (seatService.findById(seatId) == null) {
                    break;
                }

                System.out.println("Seat ID already exists!");

            } catch (Exception e) {

                System.out.println("Invalid Seat ID!");
                InputScanner.nextLine();
            }
        }

        String section;

        while (true) {

            System.out.print("Section (VIP/REGULAR): ");

            section = InputScanner.nextLine().trim();

            if (section.equalsIgnoreCase("VIP")
                    || section.equalsIgnoreCase("REGULAR")) {

                section = section.toUpperCase();

                break;
            }

            System.out.println(
                    "Invalid section! Only VIP or REGULAR allowed."
            );
        }

        String row;

        while (true) {

            System.out.print("Row (A-Z): ");

            row = InputScanner.nextLine().trim();

            if (row.matches("[A-Za-z]")) {

                row = row.toUpperCase();

                break;
            }

            System.out.println(
                    "Invalid row! Enter a single letter from A to Z."
            );
        }

        int number;

        while (true) {

            try {

                System.out.print("Seat Number: ");

                number = InputScanner.nextInt();

                if (number > 0) {
                    break;
                }

                System.out.println(
                        "Seat number must be positive!"
                );

            } catch (Exception e) {

                System.out.println(
                        "Invalid seat number!"
                );

                InputScanner.nextLine();
            }
        }

        controller.createSeat(
                eventId,
                seatId,
                section,
                row,
                number
        );
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
