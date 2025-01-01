package com.kapital.assignment.authentication_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AuthenticationServiceApplication {

	public static void main(String[] args) {
		//TODO : Remove this @Suraj
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		String rawPassword = "password123";
//		String hashedPassword = encoder.encode(rawPassword);
//		System.out.println("Hashed Password: " + hashedPassword);
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}

}
