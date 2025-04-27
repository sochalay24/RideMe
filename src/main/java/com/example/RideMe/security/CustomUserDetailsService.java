package com.example.RideMe.security;

import com.example.RideMe.entity.User;
import com.example.RideMe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Use the actual role saved in the database ("DRIVER", "PASSENGER", etc.)
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // must be encoded already
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))) // ROLE_DRIVER, ROLE_PASSENGER, etc.
                .build();
    }
}
