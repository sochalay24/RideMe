package com.example.RideMe.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    private String phoneNumber;

    @Transient
    private String role; // Selected at login: "DRIVER" or "PASSENGER"

    // One-to-Many relationship with Driver and Passenger
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Driver> driverRides;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Passenger> passengerRequests;

    // --- Getters and Setters ---

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Driver> getDriverRides() {
        return driverRides;
    }

    public void setDriverRides(List<Driver> driverRides) {
        this.driverRides = driverRides;
    }

    public List<Passenger> getPassengerRequests() {
        return passengerRequests;
    }

    public void setPassengerRequests(List<Passenger> passengerRequests) {
        this.passengerRequests = passengerRequests;
    }
}
