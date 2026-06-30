package com.ticketing.model;

import com.ticketing.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Getter
@NoArgsConstructor
@ToString
public class User {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String password;

    // =====================================================
    // BUSINESS CONSTRUCTOR (APPLICATION USE)
    // =====================================================
    public User(String name, String email, String password, Role role) {
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.password = validatePassword(password);
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    // =====================================================
    // PERSISTENCE CONSTRUCTOR (DAO USE ONLY)
    // =====================================================
    public User(Long id,
                String name,
                String email,
                Role role,
                String password) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // =====================================================
    // BUSINESS BEHAVIOR
    // =====================================================

    public void changeName(String newName) {
        this.name = validateName(newName);
    }

    public void changePassword(String newPassword) {
        this.password = validatePassword(newPassword);
    }

    public void promoteToOperator() {
        if (this.role == Role.OPERATOR) {
            throw new IllegalStateException("User is already an operator.");
        }
        this.role = Role.OPERATOR;
    }

    public void demoteToCustomer() {
        if (this.role == Role.CUSTOMER) {
            throw new IllegalStateException("User is already a customer.");
        }
        this.role = Role.CUSTOMER;
    }

    // =====================================================
    // QUERY METHODS
    // =====================================================

    public boolean isOperator() {
        return role == Role.OPERATOR;
    }

    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }

    public boolean hasEmail(String email) {
        return email != null && this.email.equalsIgnoreCase(email.trim());
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private String validateName(String name) {
        Objects.requireNonNull(name, "Name cannot be null");

        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        return name;
    }

    private String validateEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");

        email = email.trim().toLowerCase();

        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }

        return email;
    }

    private String validatePassword(String password) {
        Objects.requireNonNull(password, "Password cannot be null");

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }

        return password;
    }
}