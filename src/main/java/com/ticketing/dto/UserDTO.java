package com.ticketing.dto;

import com.ticketing.enums.Role;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;

    public UserDTO(Long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // getters only (immutable style)
}