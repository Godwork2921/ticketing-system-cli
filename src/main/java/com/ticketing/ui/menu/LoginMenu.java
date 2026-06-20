package com.ticketing.ui.menu;

import com.ticketing.enums.Role;
import com.ticketing.model.User;
import com.ticketing.ui.InputScanner;
import com.ticketing.ui.controller.LoginController;

public class LoginMenu {

    private final LoginController loginController = new LoginController();
    private final OperatorMenu operatorMenu = new OperatorMenu();
    private final CustomerMenu customerMenu = new CustomerMenu();

    public void start() {

        while (true) {

            System.out.println("\n===== LOGIN =====");
            System.out.println("1. Login");
            System.out.println("2. Register Customer");
            System.out.println("0. Exit");

            System.out.print("Choose: ");

            String input = InputScanner.nextLine();

            switch (input) {

                case "1" -> login();

                case "2" -> register();

                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }

                default -> System.out.println("Invalid choice");
            }
        }
    }


    private void login() {

        System.out.print("Email: ");
        String email = InputScanner.nextLine();

        System.out.print("Password: ");
        String password = InputScanner.nextLine();

        User user = loginController.login(email, password);

        if (user == null) {
            System.out.println("Invalid email or password");
            return;
        }

        System.out.println("Welcome " + user.getName());

        // ROLE-BASED ROUTING
        if (user.getRole() == Role.OPERATOR) {
            operatorMenu.show();
        } else if (user.getRole() == Role.CUSTOMER) {
            customerMenu.show();
        } else {
            System.out.println("Unknown role");
        }
    }

    private void register() {

        try {

            System.out.println("\n===== REGISTER CUSTOMER =====");

            System.out.print("Name: ");
            String name = InputScanner.nextLine();

            System.out.print("Email: ");
            String email = InputScanner.nextLine();

            System.out.print("Password: ");
            String password = InputScanner.nextLine();

            loginController.registerCustomer(
                    null,
                    name,
                    email,
                    password
            );

            System.out.println("Registration successful!");

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}