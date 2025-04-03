/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses, including authentication-related
 * operations like user registration and login.
 * </p>
 */
package org.arthub.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.dto.LoginUserDTO;
import org.arthub.backend.dto.RegisterUserDTO;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.responses.LoginResponse;
import org.arthub.backend.service.AuthenticationService;
import org.arthub.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling authentication-related operations.
 * <p>
 * Provides endpoints for user registration (signup) and user login, returning JWT tokens for authentication.
 * </p>
 *
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthenticationController {

    /**
     * Service for handling JWT operations such as token generation and expiration time retrieval.
     */
    private final JwtService jwtService;

    /**
     * Service for handling user authentication and registration.
     */
    private final AuthenticationService authenticationService;

    /**
     * Registers a new user.
     * <p>
     * This method handles the signup process by accepting user registration details and
     * returning the registered user. If the user already exists, an exception is thrown.
     * </p>
     *
     * @param registerUserDTO the DTO object containing user registration details
     * @return {@link ResponseEntity} containing the registered user
     * @throws AlreadyExists if the user already exists
     */
    @PostMapping("/signup")
    public ResponseEntity<User> register(
            @Validated(RegisterUserDTO.class) @RequestBody final RegisterUserDTO registerUserDTO
    ) throws AlreadyExists {
        User registeredUser = authenticationService.signup(registerUserDTO);
        return ResponseEntity.ok(registeredUser);
    }

    /**
     * Logs in a user.
     * <p>
     * This method handles the login process by accepting user login credentials,
     * authenticating the user, and returning a JWT token for future authentication.
     * </p>
     *
     * @param loginUserDTO the DTO object containing user login credentials
     * @return {@link ResponseEntity} containing the JWT token for authentication
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Validated(LoginUserDTO.class) @RequestBody final LoginUserDTO loginUserDTO
    ) {
        User authenticatedUser = authenticationService.authenticate(loginUserDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
