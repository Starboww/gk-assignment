package com.kapital.assignment.authentication_service.service;

import com.kapital.assignment.authentication_service.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    String generateToken(User user);

    Optional<User> findByUsername(String username);

    User saveUser(User user);

}
