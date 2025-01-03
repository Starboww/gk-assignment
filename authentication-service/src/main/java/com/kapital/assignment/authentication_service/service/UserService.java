package com.kapital.assignment.authentication_service.service;

import com.kapital.assignment.authentication_service.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * Service interface for managing user-related operations, including authentication and user management.
 */
public interface UserService extends UserDetailsService {

    /**
     * Generates a JWT token for the specified user.
     *
     * @param user the user for whom the token is to be generated
     * @return a JWT token as a {@link String}
     */
    String generateToken(User user);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the {@link User} if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Saves a user to the data store.
     *
     * @param user the {@link User} entity to save
     * @return the saved {@link User} entity
     */
    User saveUser(User user);

    /**
     * Hashes a raw (plain-text) password.
     *
     * @param rawPassword the plain-text password to hash
     * @return the hashed password as a {@link String}
     */
    String hashPassword(String rawPassword);

    /**
     * Checks if a username already exists in the system.
     *
     * @param username the username to check for existence
     * @return {@code true} if the username exists, {@code false} otherwise
     */
    boolean existsByUsername(String username);
}