package com.kapital.assignment.encryption_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {

            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                List<SimpleGrantedAuthority> authorities = tokenProvider.getRolesFromJWT(jwt).stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                logger.debug("Parsed Username: {}", username);
                logger.debug("Parsed Roles: {}", authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Check role-based access for the requested endpoint
              //  checkAccess(request, authorities);
            } else {
                logger.debug("JWT token is missing or invalid");
            }
        } catch (AccessDeniedException ex) {
            logger.error("Access denied: " + ex.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: " + ex.getMessage());
            return;
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void checkAccess(HttpServletRequest request, List<SimpleGrantedAuthority> authorities) {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/encrypt") && request.getMethod().equals("POST")) {
            if (!authorities.contains(new SimpleGrantedAuthority("ROLE_message_writer"))) {
                throw new AccessDeniedException("User does not have the required role: message_writer");
            }
        } else if (requestURI.startsWith("/api/encrypt/decrypt") && request.getMethod().equals("POST")) {
            if (!authorities.contains(new SimpleGrantedAuthority("ROLE_message_reader"))) {
                throw new AccessDeniedException("User does not have the required role: message_reader");
            }
        }
    }
}
