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


    private boolean isStrongPassword(String password) {

        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {

            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private boolean isValidEmail(String email) {

        if (email == null) return false;

        return email.matches("^[a-z][a-z0-9]*@gmail\\.com$");
    }

    private void register() {

        try {

            System.out.println("\n===== REGISTER CUSTOMER =====");

            String name;

            while (true) {

                System.out.print("Name: ");
                name = InputScanner.nextLine();

                if (name != null
                        && !name.trim().isEmpty()
                        && name.matches("[A-Za-z ]+")) {
                    break;
                }

                System.out.println(
                        "Invalid name! Please enter letters and spaces only."
                );
            }

            String email;

            while (true) {

                System.out.print("Email: ");
                email = InputScanner.nextLine();

                if (isValidEmail(email)) {
                    break;
                }

                System.out.println(
                        "Invalid email! Must start with lowercase letter and end with @gmail.com"
                );
            }

            String password;

            while (true) {

                System.out.print("Password: ");
                password = InputScanner.nextLine();

                if (isStrongPassword(password)) {
                    break;
                }

                System.out.println(
                        "Weak password! Must be 8+ chars, include uppercase, lowercase, number, and special character."
                );
            }

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