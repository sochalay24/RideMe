package com.example.RideMe.repository;

import com.example.RideMe.entity.Passenger;
import com.example.RideMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findByUser(User user);
    Optional<Passenger> findTopByUserOrderByDateDesc(User user);
}
