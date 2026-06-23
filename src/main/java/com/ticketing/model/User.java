package com.ticketing.model;

import com.ticketing.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String password;

    // ================= BUSINESS METHODS =================

    public boolean isOperator() {
        return role == Role.OPERATOR;
    }

    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }
}