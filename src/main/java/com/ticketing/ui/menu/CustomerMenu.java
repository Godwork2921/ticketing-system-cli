package com.ticketing.ui.menu;
import com.ticketing.ui.controller.CustomerController;
import com.ticketing.model.Event;
import com.ticketing.model.Seat;
import com.ticketing.ui.InputScanner;
import com.ticketing.util.AppContext;

public class CustomerMenu {
    private final CustomerController customerController =
            new CustomerController();
    public void show() {

        while (true) {

            System.out.println("\n========================");
            System.out.println("     CUSTOMER MENU");
            System.out.println("========================");

            System.out.println("1. View Events");
            System.out.println("2. View Available Seats");
            System.out.println("3. Reserve Seat");
            System.out.println("4. View Reservations");
            System.out.println("5. Cancel Reservation");
            System.out.println("0. Back");

            System.out.print("Choose: ");
            int choice = InputScanner.nextInt();

            switch (choice) {

                case 1 -> viewEvents();

                case 2 -> {

                    System.out.print("Event ID: ");
                    Long eventId = InputScanner.nextLong();

                    customerController
                            .viewAvailableSeats(eventId);
                }

                case 3 -> reserveSeat();

                case 4 ->
                        customerController
                                .viewMyReservations();

                case 5 -> cancelReservation();

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid choice");
            }
        }
    }

    // ================= VIEW EVENTS =================
    private void viewEvents() {

        System.out.println("\nAvailable Events:");

        AppContext.eventService.getAllEvents()
                .forEach(e ->
                        System.out.println(
                                e.getId() + " - " + e.getTitle()
                        )
                );
    }

    // ================= VIEW SEATS =================
    private void viewSeats() {

        System.out.print("Enter Event ID: ");
        Long eventId = InputScanner.nextLong();

        Event event = AppContext.eventService.findById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        System.out.println("\nAvailable Seats:");

        event.getAvailableSeats()
                .forEach(seat ->
                        System.out.println(
                                seat.getId() + " - " + seat.getSeatLabel()
                        )
                );
    }

    // ================= RESERVE SEAT =================
    private void reserveSeat() {

        System.out.print("Enter Event ID: ");
        Long eventId = InputScanner.nextLong();

        System.out.print("Enter Seat ID: ");
        Long seatId = InputScanner.nextLong();

        System.out.print("Customer Email: ");
        String email = InputScanner.nextLine();

        try {

            customerController.reserveSeat(
                    email,
                    eventId,
                    seatId
            );

            System.out.println(
                    "Reservation SUCCESS!"
            );

        } catch (Exception e) {

            System.out.println(
                    "ERROR: " + e.getMessage()
            );
        }
    }
    // ================= VIEW RESERVATIONS =================
    private void viewReservations() {

        System.out.println("\nAll Reservations:");

        AppContext.reservationService
                .getAllReservations()
                .forEach(System.out::println);
    }

    private void cancelReservation() {

        System.out.print(
                "Reservation ID: "
        );

        Long reservationId =
                InputScanner.nextLong();

        try {

            customerController
                    .cancelReservation(
                            reservationId
                    );

        } catch (Exception e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

}