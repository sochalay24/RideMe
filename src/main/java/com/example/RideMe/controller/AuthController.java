package com.example.RideMe.controller;

import com.example.RideMe.dto.DriverProfileForm;
import com.example.RideMe.dto.RoleSelection;
import com.example.RideMe.entity.DriverProfile;
import com.example.RideMe.entity.User;
import com.example.RideMe.repository.DriverProfileRepository;
import com.example.RideMe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverProfileRepository driverProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user, Model model) {
        if (!user.getEmail().endsWith("@mitwpu.edu.in")) {
            model.addAttribute("error", "Only @mitwpu.edu.in emails are allowed.");
            return "signup";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already registered.");
            return "signup";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already taken.");
            return "signup";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/selectrole")
    public String selectRoleForm(Model model) {
        model.addAttribute("roleSelection", new RoleSelection());
        return "selectrole";
    }

    @PostMapping("/selectrole")
    public String processRoleSelection(@ModelAttribute RoleSelection form, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        user.setRole(form.getRole());
        userRepository.save(user);

        if ("DRIVER".equalsIgnoreCase(form.getRole())) {
            return "redirect:/driversetup";
        } else {
            return "redirect:/passenger/home";
        }
    }

    @GetMapping("/driversetup")
    public String driverSetupForm(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Optional<DriverProfile> existing = driverProfileRepository.findByUser(user);

        DriverProfileForm form = new DriverProfileForm();
        existing.ifPresent(profile -> {
            form.setVehicleNumber(profile.getVehicleNumber());
            form.setVehicleType(profile.getVehicleType());
            form.setDepartment(profile.getDepartment());
        });

        model.addAttribute("driverProfile", form);
        return "driversetup";
    }

    @PostMapping("/driversetup")
    public String processDriverSetup(@ModelAttribute DriverProfileForm form, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        DriverProfile profile = driverProfileRepository.findByUser(user).orElse(new DriverProfile());
        profile.setUser(user);
        profile.setVehicleType(form.getVehicleType());
        profile.setVehicleNumber(form.getVehicleNumber());
        profile.setDepartment(form.getDepartment());
        driverProfileRepository.save(profile);

        return "redirect:/driver/home";
    }
}
