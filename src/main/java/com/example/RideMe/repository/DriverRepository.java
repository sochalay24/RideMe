package com.example.RideMe.repository;

import com.example.RideMe.entity.Driver;
import com.example.RideMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByUser(User user);
    Optional<Driver> findTopByUserOrderByRideDateDesc(User user);
    List<Driver> findByUserOrderByRideDateDesc(User user);
    List<Driver> findByUserAndRideDateAfter(User user, LocalDate date);



}
