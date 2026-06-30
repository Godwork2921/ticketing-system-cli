package com.ticketing.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Getter
@NoArgsConstructor
@ToString
public class Venue {

    private Long id;

    private String name;
    private String address;
    private String timezone;

    // =====================================================
    // BUSINESS CONSTRUCTOR
    // =====================================================
    public Venue(String name, String address, String timezone) {

        this.name = validateText(name, "name");
        this.address = validateText(address, "address");
        this.timezone = validateText(timezone, "timezone");
    }

    // =====================================================
    // PERSISTENCE CONSTRUCTOR (USED BY DAO)
    // =====================================================
    public Venue(Long id, String name, String address, String timezone) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.timezone = timezone;
    }

    // =====================================================
    // BUSINESS METHODS
    // =====================================================

    public void rename(String newName) {
        this.name = validateText(newName, "name");
    }

    public void relocate(String newAddress) {
        this.address = validateText(newAddress, "address");
    }

    public void changeTimezone(String newTimezone) {
        this.timezone = validateText(newTimezone, "timezone");
    }

    // =====================================================
    // QUERY METHODS
    // =====================================================

    public boolean hasName(String name) {
        return name != null && this.name.equalsIgnoreCase(name.trim());
    }

    public boolean isInTimezone(String timezone) {
        return timezone != null && this.timezone.equalsIgnoreCase(timezone.trim());
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private String validateText(String value, String field) {

        Objects.requireNonNull(value, field + " cannot be null");

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }

        return value;
    }
}