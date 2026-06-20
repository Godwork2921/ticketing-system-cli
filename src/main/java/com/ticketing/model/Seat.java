package com.ticketing.model;

import com.ticketing.enums.SeatStatus;

public class Seat {

    private Long id;
    private Long eventId;   // IMPORTANT FIX
    private String section;
    private String row;
    private int number;
    private SeatStatus status;

    public Seat() {}

    public Seat(Long id, Long eventId, String section, String row, int number, SeatStatus status) {
        this.id = id;
        this.eventId = eventId;
        this.section = section;
        this.row = row;
        this.number = number;
        this.status = status;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public String getSeatLabel() {
        return section + "-" + row + "-" + number;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", section='" + section + '\'' +
                ", row='" + row + '\'' +
                ", number=" + number +
                ", status=" + status +
                '}';
    }
}