package com.example.RideMe.controller;

import com.example.RideMe.dto.DriverRideForm;
import com.example.RideMe.entity.Driver;
import com.example.RideMe.entity.DriverProfile;
import com.example.RideMe.entity.User;
import com.example.RideMe.repository.DriverProfileRepository;
import com.example.RideMe.repository.DriverRepository;
import com.example.RideMe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DriverProfileRepository driverProfileRepo;

    @Autowired
    private DriverRepository driverRepo;

    // Show Driver Setup (Merged with driver-details)


    // Save Driver Setup
    @GetMapping("/setup")
    public String showDriverSetupForm(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        DriverProfile existingProfile = driverProfileRepo.findByUser(user).orElse(null);

        if (existingProfile != null) {
            model.addAttribute("vehicleType", existingProfile.getVehicleType());
            model.addAttribute("vehicleNumber", existingProfile.getVehicleNumber());
            model.addAttribute("department", existingProfile.getDepartment());
        }

        return "driversetup";
    }

    @PostMapping("/setup")
    public String saveOrUpdateDriverProfile(@RequestParam(required = false) String vehicleType,
                                            @RequestParam(required = false) String vehicleNumber,
                                            @RequestParam(required = false) String department,
                                            @RequestParam(required = false) String phoneNumber,
                                            @RequestParam(required = false) String action,
                                            Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            user.setPhoneNumber(phoneNumber);
            userRepo.save(user);
        }

        DriverProfile profile = driverProfileRepo.findByUser(user).orElse(new DriverProfile());

        if (profile.getUser() == null) {
            profile.setUser(user); // Important for new profiles
        }

        // Save or update vehicle info regardless of "action"
        profile.setVehicleType(vehicleType);
        profile.setVehicleNumber(vehicleNumber);
        profile.setDepartment(department);
        profile.setPhoneNumber(phoneNumber);

        driverProfileRepo.save(profile);

        return "redirect:/driver/home";
    }


    // Driver Home Page - Ride Submission Form
    @GetMapping("/home")
    public String showDriverHome(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        DriverProfile profile = driverProfileRepo.findByUser(user).orElse(null);

        if (profile == null) {
            return "redirect:/driver/setup";
        }

        model.addAttribute("driverRideForm", new DriverRideForm());
        model.addAttribute("driverProfile", profile);

        return "driverhome";
    }

    // Handle Daily Ride Submission
    @PostMapping("/submitride")
    public String submitRide(@ModelAttribute DriverRideForm form, Principal principal, Model model) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        DriverProfile profile = driverProfileRepo.findByUser(user).orElseThrow();

        Driver ride = new Driver();
        ride.setUser(user);
        ride.setRideDate(form.getRideDate());
        ride.setDepartureTime(form.getDepartureTime());
        ride.setPickupLocation(form.getPickupLocation());
        ride.setLandmark(form.getLandmark());  // Set landmark if 'Other' location is chosen
        ride.setSeatsAvailable(form.getSeatsAvailable());
        ride.setDirection(form.getDirection());
        ride.setVehicleNumber(profile.getVehicleNumber());
        ride.setVehicleType(profile.getVehicleType());
        ride.setPhoneNumber(profile.getPhoneNumber());

        driverRepo.save(ride);

        // Pass ride object to confirmation page
        model.addAttribute("ride", ride);

        return "rideconfirmation";
    }

    // Driver Profile Page to use existing vehicle details (if any)
    @GetMapping("/usepreviousinfo")
    public String usePreviousVehicleInfo(Principal principal, Model model) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        // Retrieve the driver's profile
        DriverProfile profile = driverProfileRepo.findByUser(user).orElse(null);

        if (profile != null) {
            // Set the form fields with the saved vehicle info
            model.addAttribute("vehicleType", profile.getVehicleType());
            model.addAttribute("vehicleNumber", profile.getVehicleNumber());
            model.addAttribute("department", profile.getDepartment());
        } else {
            model.addAttribute("message", "No previous vehicle info found.");
        }

        return "driversetup";  // Show the setup page with pre-filled data
    }

    // Show Dashboard with Ride History
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        // Fetch the list of rides for this driver (the logged-in user)
        List<Driver> rides = driverRepo.findByUser(user);

        // Pass the rides to the view (dashboard.html)
        model.addAttribute("rides", rides);
        return "dashboard";  // This will pass the rides to the dashboard view
    }
    @PostMapping("/deleteride/{rideId}")
    public String deleteRide(@PathVariable Long rideId, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        Driver ride = driverRepo.findById(rideId).orElse(null);

        if (ride != null && ride.getUser().equals(user) && !ride.getRideDate().isBefore(LocalDate.now())) {
            driverRepo.delete(ride);
        }

        return "redirect:/driver/dashboard?deleted";
    }

}
