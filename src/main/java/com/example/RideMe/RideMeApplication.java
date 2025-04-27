package com.example.RideMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = "com.example.RideMe.repository")


@SpringBootApplication
public class RideMeApplication {


	public static void main(String[] args) {
		SpringApplication.run(RideMeApplication.class, args);
	}

}
