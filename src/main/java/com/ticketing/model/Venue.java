package com.ticketing.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Venue {

    private Long id;
    private String name;
    private String address;
    private String timezone;
}