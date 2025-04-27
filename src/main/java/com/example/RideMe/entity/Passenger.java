package com.example.RideMe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "passenger_requests")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pickupLocation;
    private String landmark;
    private LocalDate date;
    private int seatsRequested;
    private String phoneNumber;

    private String direction; // "TO_UNIVERSITY" or "FROM_UNIVERSITY"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getSeatsRequested() {
        return seatsRequested;
    }

    public void setSeatsRequested(int seatsRequested) {
        this.seatsRequested = seatsRequested;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
