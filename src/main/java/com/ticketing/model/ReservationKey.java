
package com.ticketing.model;

public record ReservationKey(String email, Long eventId, Long seatId) {
    @Override
    public String toString() {
        return (email == null ? "" : email.toLowerCase().trim())
                + "-" + eventId + "-" + seatId;
    }
}
