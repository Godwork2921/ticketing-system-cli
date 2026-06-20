package com.ticketing.ui;

import java.util.Scanner;

public class InputScanner {

    private static final Scanner scanner = new Scanner(System.in);

    public static String nextLine() {

        String input = scanner.nextLine();

        while (input == null || input.trim().isEmpty()) {
            System.out.print("Input cannot be empty. Try again: ");
            input = scanner.nextLine();
        }

        return input.trim();
    }

    public static int nextInt() {

        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    public static long nextLong() {

        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);

            } catch (NumberFormatException e) {
                System.out.print("Enter a valid long number: ");
            }
        }
    }
}