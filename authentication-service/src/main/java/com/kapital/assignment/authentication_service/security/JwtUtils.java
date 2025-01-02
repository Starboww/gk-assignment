package com.kapital.assignment.authentication_service.security;

import com.kapital.assignment.authentication_service.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token including username and userId.
     *
     * @param authentication The authentication object.
     * @param userId         The unique identifier of the user.
     * @return The generated JWT token.
     */
    public String generateJwtToken(Authentication authentication, Long userId) {
        org.springframework.security.core.userdetails.User userPrincipal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", String.join(",", roles));
        claims.put("userId", userId); // Embed userId in the token

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token for a given user.
     * This method can be utilized by the UserService to generate tokens.
     *
     * @param user The user entity.
     * @return The generated JWT token.
     */
    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", String.join(",", roles));
        claims.put("userId", user.getId()); // Embed userId in the token

        return Jwts.builder()
                .setSubject(user.getUsername())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token The JWT token.
     * @return The username.
     */
    public String getUsernameFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts the roles from the JWT token.
     *
     * @param token The JWT token.
     * @return A list of roles.
     */
    public List<SimpleGrantedAuthority> getRolesFromJwt(String token) {
        String roles = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", String.class);

        return Arrays.stream(roles.split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    /**
     * Extracts the userId from the JWT token.
     *
     * @param token The JWT token.
     * @return The userId.
     */
    public Long getUserIdFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    /**
     * Validates the JWT token.
     *
     * @param authToken The JWT token.
     * @return True if valid, else false.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException |
                 IllegalArgumentException | ExpiredJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

}
