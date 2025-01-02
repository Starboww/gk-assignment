package com.kapital.assignment.authentication_service.controller;

import com.kapital.assignment.authentication_service.dto.AuthRequest;
import com.kapital.assignment.authentication_service.dto.AuthResponse;
import com.kapital.assignment.authentication_service.dto.RegistrationRequest;
import com.kapital.assignment.authentication_service.dto.RegistrationResponse;
import com.kapital.assignment.authentication_service.entity.User;
import com.kapital.assignment.authentication_service.security.JwtUtils;
import com.kapital.assignment.authentication_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;


    @PostMapping("/token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            User user = userService.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtils.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        // Check if the username is already taken
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(RegistrationResponse.builder()
                            .message("Username is already in use")
                            .build());
        }

        try {

            User user = new User();
            user.setUsername(registrationRequest.getUsername());
            user.setPasswordHash(userService.hashPassword(registrationRequest.getPassword()));
            Set<String> roles = registrationRequest.getRoles().stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
            user.setRoles(roles);

            userService.saveUser(user);


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registrationRequest.getUsername(),
                            registrationRequest.getPassword()
                    )
            );

            String token = jwtUtils.generateJwtToken(authentication, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RegistrationResponse.builder()
                            .token(token)
                            .message("User registered successfully")
                            .build());

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RegistrationResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

}
