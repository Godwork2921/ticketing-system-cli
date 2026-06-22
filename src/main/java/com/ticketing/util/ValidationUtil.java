package com.ticketing.util;

public class ValidationUtil {

    // =========================
    // EVENT TITLE VALIDATION
    // =========================
    public static boolean isValidEventTitle(String title) {
        return title != null && title.matches("[A-Za-z0-9 ]+");
    }


    // =========================
    // VENUE ID VALIDATION
    // =========================
    public static boolean isValidVenueId(Long id) {
        return isValidId(id);
    }

    // =========================
    // EVENT TIME VALIDATION
    // =========================
    public static boolean isValidEventTime(java.time.LocalDateTime start,
                                           java.time.LocalDateTime end) {

        return start != null
                && end != null
                && end.isAfter(start);
    }


    // =========================
    // EVENT CONFLICT VALIDATION
    // Same venue + same time = NOT allowed
    // =========================
    public static boolean isEventTimeConflict(
            java.util.List<?> events,
            Long venueId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    ) {
        return false; // implement in service layer (explained below)
    }







    // =========================
    // NAME VALIDATION
    // =========================
    public static boolean isValidName(String name) {
        return name != null
                && name.matches("[A-Za-z ]+");
    }

    // =========================
    // VENUE ID VALIDATION
    // =========================
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    // =========================
    // ADDRESS VALIDATION
    // Example: "Bole Road 123"
    // =========================
    public static boolean isValidAddress(String address) {
        return address != null
                && address.matches("[A-Za-z0-9 ]+");
    }

    // =========================
    // TIMEZONE VALIDATION
    // Only real IANA timezones
    // =========================
    public static boolean isValidTimezone(String tz) {
        return tz != null && tz.matches(
                "Africa/[A-Za-z_]+|Europe/[A-Za-z_]+|America/[A-Za-z_]+|Asia/[A-Za-z_]+"
        );
    }

    // =========================
    // SECTION VALIDATION (VIP/REGULAR)
    // =========================
    public static String normalizeSection(String section) {

        if (section == null) return null;

        section = section.trim().toUpperCase();

        if (section.equals("VIP") || section.equals("REGULAR")) {
            return section;
        }

        return null;
    }

    // =========================
    // ROW VALIDATION (A-Z)
    // =========================
    public static String normalizeRow(String row) {

        if (row == null) return null;

        row = row.trim().toUpperCase();

        if (row.matches("[A-Z]")) {
            return row;
        }

        return null;
    }
}