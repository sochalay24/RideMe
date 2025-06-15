package com.example.RideMe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String passengerEmail, String driverName, String driverPhone, String rideDetails) {
        // Create the email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(passengerEmail);  // The passenger's email
        message.setSubject("Ride Confirmation - RideMe");

        // Customize the content for passenger
        String emailContent = "Hello, your ride has been confirmed with " + driverName + ".\n"
                + "Driver Contact: " + driverPhone + "\n\n"
                + rideDetails + "\n\n"
                + "Please contact the driver directly if needed.\n"
                + "Thank you for using RideMe!";

        message.setText(emailContent);

        // Send the email
        javaMailSender.send(message);
    }



    public void sendConfirmationEmail(String driverEmail, String passengerName, String passengerPhone,String rideDetails) {
        // Create a SimpleMailMessage object to set up the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(driverEmail);  // The driver's email
        message.setSubject("Ride Confirmation - Passenger Confirmed");

        // Customize the content of the email
        String emailContent = "Hello, " + passengerName + " has confirmed the ride.\n"
                + "Passenger Phone: " + passengerPhone + "\n\n"
                + rideDetails;

        message.setText(emailContent);

        // Send the email
        javaMailSender.send(message);
    }
}
