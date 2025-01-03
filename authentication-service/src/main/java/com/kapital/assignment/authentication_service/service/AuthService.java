package com.kapital.assignment.authentication_service.service;

import com.kapital.assignment.authentication_service.dto.AuthRequest;
import com.kapital.assignment.authentication_service.dto.AuthResponse;
import com.kapital.assignment.authentication_service.dto.RegistrationRequest;
import com.kapital.assignment.authentication_service.dto.RegistrationResponse;
import com.kapital.assignment.authentication_service.entity.User;
import com.kapital.assignment.authentication_service.exception.ConflictException;
import com.kapital.assignment.authentication_service.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        User user = userService.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token);
    }

    public RegistrationResponse registerUser(RegistrationRequest registrationRequest) throws ConflictException {
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            throw new ConflictException("Username is already in use");
        }
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
        return RegistrationResponse.builder()
                .token(token)
                .message("User registered successfully")
                .build();
    }

}
