package com.example.RideMe.controller;

import com.example.RideMe.entity.User;
import com.example.RideMe.repository.DriverProfileRepository;
import com.example.RideMe.repository.DriverRepository;
import com.example.RideMe.repository.PassengerRepository;
import com.example.RideMe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverProfileRepository driverProfileRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null || user.getRole() == null || user.getRole().isBlank()) {
            return "redirect:/selectrole";
        }

        model.addAttribute("name", user.getName());
        model.addAttribute("role", user.getRole());

        if ("DRIVER".equalsIgnoreCase(user.getRole())) {
            // Driver profile info (for prefilled fields)
            driverProfileRepository.findByUser(user).ifPresent(profile ->
                    model.addAttribute("vehicleInfo", profile)
            );

            // Show latest ride info if exists
            driverRepository.findTopByUserOrderByRideDateDesc(user).ifPresent(ride ->
                    model.addAttribute("latestRide", ride)
            );

            return "driverhome";

        } else if ("PASSENGER".equalsIgnoreCase(user.getRole())) {
            // Show passenger's recent search info
            passengerRepository.findTopByUserOrderByDateDesc(user).ifPresent(search ->
                    model.addAttribute("latestSearch", search)
            );

            return "passengerhome";
        }

        return "home";
    }
}
