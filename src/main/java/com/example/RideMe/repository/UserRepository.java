package com.example.RideMe.repository;

import com.example.RideMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);         // Used for login and user identification

    Optional<User> findByUsername(String username);   // Optional: if you use username for lookup

    boolean existsByEmail(String email);              // Useful during signup to check duplicates

    boolean existsByUsername(String username);        // Useful during signup to check duplicates
}
