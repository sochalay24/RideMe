package com.example.RideMe.repository;

import com.example.RideMe.entity.DriverProfile;
import com.example.RideMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    Optional<DriverProfile> findByUser(User user);
    boolean existsByUser(User user);

}
