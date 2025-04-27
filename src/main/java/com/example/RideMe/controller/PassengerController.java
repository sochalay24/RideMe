package com.example.RideMe.controller;

import com.example.RideMe.entity.Driver;
import com.example.RideMe.entity.Passenger;
import com.example.RideMe.entity.User;
import com.example.RideMe.repository.DriverRepository;
import com.example.RideMe.repository.PassengerRepository;
import com.example.RideMe.repository.UserRepository;
import com.example.RideMe.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    private MailService mailService;


    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private DriverRepository driverRepo;

    // Passenger homepage with ride search form
    @GetMapping("/home")
    public String showPassengerHome(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        Passenger existing = passengerRepo.findTopByUserOrderByDateDesc(user).orElse(null);
        if (existing != null) {
            model.addAttribute("latestRequest", existing); // pre-fill form if needed
        }

        return "passengerhome";
    }

    // Submit ride request (basic storage)
    @PostMapping("/submitrequest")
    public String submitRideRequest(@RequestParam("pickupLocation") String pickupLocation,
                                    @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                    @RequestParam("seatsRequired") int seatsRequired,
                                    @RequestParam String phoneNumber,
                                    @RequestParam("direction") String direction,
                                    @RequestParam(value = "landmark", required = false) String landmark,
                                    Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        user.setPhoneNumber(phoneNumber);
        userRepo.save(user);
        Passenger request = new Passenger();
        request.setUser(user);
        request.setPickupLocation(pickupLocation);
        request.setDate(date);
        request.setSeatsRequested(seatsRequired);
        request.setDirection(direction);
        request.setLandmark(landmark);
        request.setPhoneNumber(phoneNumber);


        passengerRepo.save(request);

        return "redirect:/passenger/home?success";
    }


    // Confirm ride request from available search results
    @PostMapping("/confirmrequest")
    public String confirmRideRequest(@RequestParam Long rideId,
                                     @RequestParam int seatsRequested,
                                     @RequestParam String phoneNumber, // Add phone number to the request
                                     Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        Optional<Driver> rideOpt = driverRepo.findById(rideId);
        if (rideOpt.isEmpty()) {
            return "redirect:/passenger/home?error=ride-not-found";
        }

        Driver ride = rideOpt.get();

        if (ride.getSeatsAvailable() < seatsRequested) {
            return "redirect:/passenger/home?error=not-enough-seats";
        }

        // Decrease available seats
        int updatedSeats = ride.getSeatsAvailable() - seatsRequested;
        ride.setSeatsAvailable(updatedSeats);

        if (updatedSeats == 0) {
            driverRepo.delete(ride); // Remove the ride if no seats left
        } else {
            driverRepo.save(ride); // Otherwise just update the count
        }

        // Save confirmed passenger request with phone number
        Passenger passenger = new Passenger();
        passenger.setUser(user);
        passenger.setPickupLocation(ride.getPickupLocation());
        passenger.setDate(ride.getRideDate());
        passenger.setSeatsRequested(seatsRequested);
        passenger.setDirection(ride.getDirection());
        passenger.setPhoneNumber(phoneNumber); // Store the passenger's phone number

        passengerRepo.save(passenger);

        return "redirect:/passenger/home?success=confirmed";
    }

    // Show all available rides
    // Show all available rides (future rides with available seats)
    @GetMapping("/rides/available")
    public String showAllAvailableRides(Model model) {
        LocalDate today = LocalDate.now();  // Get today's date

        // Fetch all rides and filter out past rides and rides with no available seats
        List<Driver> availableRides = driverRepo.findAll().stream()
                .filter(ride -> !ride.getRideDate().isBefore(today) && ride.getSeatsAvailable() > 0)
                .collect(Collectors.toList());

        model.addAttribute("rides", availableRides);
        return "searchresults";
    }

    // Search for rides based on parameters (date, location, time)
    @GetMapping("/rides/search")
    public String searchRides(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                              @RequestParam(required = false) String time,
                              @RequestParam(required = false) String location,
                              @RequestParam(required = false) String landmark,
                              Model model) {
        LocalDate today = LocalDate.now();  // Get today's date

        // Fetch all rides and filter based on user input and excluding past rides/no available seats
        List<Driver> filteredRides = driverRepo.findAll().stream()
                .filter(ride -> (date == null || ride.getRideDate().isEqual(date) || ride.getRideDate().isAfter(today)) &&
                        (location == null || location.isBlank() || ride.getPickupLocation().toLowerCase().contains(location.toLowerCase())) &&
                        ride.getSeatsAvailable() > 0)  // Filter for rides with available seats
                .collect(Collectors.toList());

        model.addAttribute("rides", filteredRides);
        return "searchresults";
    }

    @PostMapping("/confirmride")
    public String confirmRideRequest(@RequestParam Long rideId,
                                     @RequestParam int seatsRequested,
                                     Principal principal,
                                     Model model) {

        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        Optional<Driver> rideOpt = driverRepo.findById(rideId);

        if (rideOpt.isPresent()) {
            Driver ride = rideOpt.get();
            int updatedSeats = ride.getSeatsAvailable() - seatsRequested;

            if (updatedSeats >= 0) {
                ride.setSeatsAvailable(updatedSeats);
                driverRepo.save(ride);

                // Save passenger request
                Passenger passenger = new Passenger();
                passenger.setUser(user);
                passenger.setPickupLocation(ride.getPickupLocation());
                passenger.setDate(ride.getRideDate());
                passenger.setSeatsRequested(seatsRequested);
                passenger.setDirection(ride.getDirection());


                passengerRepo.save(passenger);

                String passengerPhone = user.getPhoneNumber();
                String passengerName = user.getName();
                String driverEmail = ride.getUser().getEmail();

                // Email content
                String rideDetails = "Ride Date: " + ride.getRideDate() +
                        "\nDeparture Time: " + ride.getDepartureTime() +
                        "\nPickup Location: " + ride.getPickupLocation() +
                        "\nSeats Available: " + ride.getSeatsAvailable() +
                        "\nVehicle: " + ride.getVehicleType() + " " + ride.getVehicleNumber() +
                        "\nPassenger Phone: " + passengerPhone;  // <-- 📱 Use from request

              

                mailService.sendConfirmationEmail(driverEmail, passengerName, passengerPhone, rideDetails);


                model.addAttribute("ride", ride);
                model.addAttribute("seatsRequested", seatsRequested);

                return "confirmride";
            } else {
                model.addAttribute("message", "Not enough seats available.");
                return "searchresults";
            }
        }

        return "redirect:/passenger/home?error=ride-not-found";
    }

}
