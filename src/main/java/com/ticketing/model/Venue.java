package com.ticketing.model;

public class Venue {

    private Long id;
    private String name;
    private String address;
    private String timezone;

    public Venue() {
    }

    public Venue(Long id, String name, String address, String timezone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.timezone = timezone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }
}