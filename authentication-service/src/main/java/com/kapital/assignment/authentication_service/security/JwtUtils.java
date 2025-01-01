package com.kapital.assignment.authentication_service.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User userPrincipal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
        claims.put("roles", String.join(",", roles));

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public String getUsernameFromJwt(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public List<SimpleGrantedAuthority> getRolesFromJwt(String token) {
        String roles = (String) Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("roles");

        return Arrays.stream(roles.split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
           // log.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            //log.error("Expired JWT token", e);
            // JWT token is expired
        } catch (UnsupportedJwtException e) {
            //log.error("Unsupported JWT token", e);
            // JWT token is unsupported
        } catch (IllegalArgumentException e) {
           // log.error("JWT claims string is empty", e);
            // JWT claims string is empty
        }
        return false;
    }

}
