# University Carpooling Web Application

A web application built using **Java**, **Spring Boot**, **Thymeleaf**, and **MySQL**, designed to help university students, faculty, and staff share rides efficiently and securely. Users can sign up as drivers or passengers, offer or request rides, and manage daily commuting needs.

## Features

### User Authentication
- Secure sign-up and login using Spring Security
- Role-based access: **Driver** or **Passenger**
- Email domain restriction (e.g., `@mitwpu.edu.in`)

### User Profile Management
- Name, username, email, and gender collected at registration
- Drivers provide vehicle details (once reused later)
- Role selection on each login (Driver or Passenger for the day)

### Driver Module
- Add new ride: Date, Time, Location, Landmark, Vehicle Type/Number, Seats Available
- Direction selection: University ➝ Location or Location ➝ University
- Ride history view
- Reuse saved vehicle details

### Passenger Module
- Search for available rides based on location and date
- Request a ride with number of seats
- View confirmed rides
- Reuse saved preferred pickup location

### Notifications
- Email sent to the driver when a passenger confirms a ride (via Gmail SMTP or campus email)

### Technologies Used
- Java 17+
- Spring Boot (MVC, Security, Data JPA)
- Thymeleaf for dynamic HTML rendering
- MySQL for relational data storage
- Maven for dependency management

## Project Structure

src/
├── main/
│ ├── java/com/carpool/
│ │ ├── controller/ # Controllers for auth, home, driver, passenger
│ │ ├── model/ # Entities: User, Driver, Passenger, Ride
│ │ ├── repository/ # Spring Data JPA Repositories
│ │ ├── service/ # Business logic
│ │ └── CarpoolApp.java # Main class
│ └── resources/
│ ├── templates/ # Thymeleaf HTML files
│ │ ├── login.html
│ │ ├── signup.html
│ │ ├── home.html
│ │ ├── selectrole.html
│ │ ├── driverhome.html
│ │ ├── passengerhome.html
│ │ ├── submitride.html
│ │ ├── requestride.html
│ │ └── ...
│ ├── application.properties
│ └── static/ # CSS, JS, images
