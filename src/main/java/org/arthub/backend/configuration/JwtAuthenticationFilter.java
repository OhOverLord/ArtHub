/**
 * This package contains configuration classes for the Arthub backend application.
 * <p>
 * The configurations include security-related classes such as JWT authentication filters
 * and other security components.
 * </p>
 */
package org.arthub.backend.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.arthub.backend.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Filter class for handling JWT authentication.
 * <p>
 * This filter intercepts every request to check for a valid JWT token in the
 * Authorization header. If a valid token is found, it extracts the username
 * and sets the authentication context.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Resolver for handling exceptions during filter execution.
     */
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Service for handling JWT operations such as token validation and username extraction.
     */
    private final JwtService jwtService;

    /**
     * Service for loading user details used for authentication.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Filters incoming HTTP requests to check for valid JWT tokens in the Authorization header.
     * <p>
     * If a valid JWT token is found, it extracts the username, loads user details, and sets the
     * authentication context for the request. If an error occurs, it is resolved using the
     * {@link HandlerExceptionResolver}.
     * </p>
     *
     * @param request     the {@link HttpServletRequest}
     * @param response    the {@link HttpServletResponse}
     * @param filterChain the {@link FilterChain} to continue with the next filter in the chain
     * @throws ServletException if an error occurs during servlet processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // If Authorization header is missing or doesn't start with "Bearer ", continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT from the header and retrieve the username
            final String jwt = authHeader.substring(7);
            final String userName = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If the username is not null and the user is not already authenticated
            if (userName != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

                // If the JWT token is valid, set the authentication context
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
