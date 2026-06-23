package com.ticketing.model;

import com.ticketing.enums.SeatStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Seat {

    private Long id;
    private Long eventId;   // IMPORTANT FIX
    private String section;
    private String row;
    private int number;
    private SeatStatus status;

    // ================= BUSINESS METHODS =================

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public String getSeatLabel() {
        return section + "-" + row + "-" + number;
    }
}