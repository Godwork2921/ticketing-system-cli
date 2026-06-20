package com.ticketing.enums;

public enum ReservationStatus {

        HOLD,
        CONFIRMED,
        CANCELLED;

        public boolean isFinalStatus() {
            return this == CONFIRMED || this == CANCELLED;
        }
    }