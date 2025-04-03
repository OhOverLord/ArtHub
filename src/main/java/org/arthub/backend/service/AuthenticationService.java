package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.dto.LoginUserDTO;
import org.arthub.backend.dto.RegisterUserDTO;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.messages.LogMessages;
import org.arthub.backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for user authentication and registration.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    /**
     * Repository for managing user data in the database.
     */
    private final UserRepository userRepository;

    /**
     * Service for encoding and decoding user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authentication manager to handle user authentication.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     *
     * @param registerUserDTO the DTO containing user registration details
     * @return the newly registered user
     * @throws AlreadyExists if a user with the same username or email already exists
     */
    public User signup(final RegisterUserDTO registerUserDTO) throws AlreadyExists {
        log.info(LogMessages.ATTEMPT_USER_REGISTRATION.getMessage(), registerUserDTO.getUsername());

        if (userRepository.findByUsername(registerUserDTO.getUsername()).isPresent()
                || userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            log.warn(LogMessages.USER_REGISTRATION_FAILED.getMessage(),
                    registerUserDTO.getUsername(), registerUserDTO.getEmail());
            throw new AlreadyExists();
        }

        User user = new User();
        user.setEmail(registerUserDTO.getEmail());
        user.setUsername(registerUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));

        User savedUser = userRepository.save(user);
        log.info(LogMessages.USER_REGISTERED_SUCCESS.getMessage(), savedUser.getUsername());

        return savedUser;
    }


    /**
     * Authenticates a user.
     *
     * @param loginUserDTO the DTO containing user login details
     * @return the authenticated user
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    public User authenticate(final LoginUserDTO loginUserDTO) {
        log.info(LogMessages.ATTEMPT_USER_AUTHENTICATION.getMessage(), loginUserDTO.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword())
        );

        User user = userRepository.findByUsername(loginUserDTO.getUsername()).orElse(null);
        if (user != null) {
            log.info(LogMessages.USER_AUTHENTICATED_SUCCESS.getMessage(), user.getUsername());
        } else {
            log.warn(LogMessages.USER_NOT_FOUND_AFTER_AUTHENTICATION.getMessage(), loginUserDTO.getUsername());
        }

        return user;
    }
}
