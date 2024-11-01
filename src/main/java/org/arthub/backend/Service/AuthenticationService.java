package org.arthub.backend.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.DTO.LoginUserDTO;
import org.arthub.backend.DTO.RegisterUserDTO;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Exception.AlreadyExists;
import org.arthub.backend.Repository.UserRepository;
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
        log.info("Attempting to register a new user with username: {}", registerUserDTO.getUsername());

        if (userRepository.findByUsername(registerUserDTO.getUsername()).isPresent()
                || userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            log.warn("Registration failed: User with username '{}' or email '{}' already exists.",
                    registerUserDTO.getUsername(), registerUserDTO.getEmail());
            throw new AlreadyExists();
        }

        User user = new User();
        user.setEmail(registerUserDTO.getEmail());
        user.setUsername(registerUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with username: {}", savedUser.getUsername());

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
        log.info("Authenticating user with username: {}", loginUserDTO.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword())
        );

        User user = userRepository.findByUsername(loginUserDTO.getUsername()).orElse(null);
        if (user != null) {
            log.info("User '{}' authenticated successfully.", user.getUsername());
        } else {
            log.warn("User '{}' not found after authentication.", loginUserDTO.getUsername());
        }

        return user;
    }

}
