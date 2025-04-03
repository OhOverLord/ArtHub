/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to user operations,
 * including account management, adding tags to users, updating user information,
 * and deleting users.
 * </p>
 */
package org.arthub.backend.controller;

import lombok.RequiredArgsConstructor;
import org.arthub.backend.dto.PasswordUpdateRequestDTO;
import org.arthub.backend.dto.UserProfileDTO;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.ConfirmPasswordIsNotEqual;
import org.arthub.backend.exception.InvalidPassword;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.facade.UserFacade;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.service.JwtService;
import org.arthub.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing user-related operations.
 * <p>
 * Provides endpoints for retrieving user account information, adding tags to a user,
 * updating user information, and deleting a user.
 * </p>
 *
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    /**
     * Service for handling user-related logic.
     */
    private final UserService service;

    /**
     * Facade for performing complex user-related operations.
     */
    private final UserFacade facade;

    /**
     * Service for handling JWT operations such as token generation.
     */
    private final JwtService jwtService;

    /**
     * Retrieves the currently authenticated user's account information.
     *
     * @return {@link ResponseEntity} containing the current {@link User}
     */
    @GetMapping("/account")
    public ResponseEntity<User> getUserByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Adds tags to the currently authenticated user.
     *
     * @param tagIds the {@link List} of tag IDs to be added to the user
     * @return {@link ResponseEntity} containing the updated {@link User}
     */
    @PostMapping("/tags")
    public ResponseEntity<User> addTags(@RequestBody final List<Long> tagIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        facade.addTagsToUser(currentUser, tagIds);
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Updates the currently authenticated user's information.
     *
     * @param updateUser the {@link User} object containing the updated information
     * @return {@link ResponseEntity} containing a map with the new token and success message
     * @throws AlreadyExists if the user already exists
     */
    @PutMapping
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody final User updateUser) throws AlreadyExists {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        User updatedUser = service.update(currentUser, updateUser);
        String newToken = jwtService.generateToken(updatedUser);
        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);
        response.put("message", "User updated successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for updating the user's password.
     *
     * @param request the {@link PasswordUpdateRequestDTO} containing the current password, new password,
     * and password confirmation.
     * @return a {@link ResponseEntity} with a status message indicating the success of the operation.
     * @throws InvalidPassword if the current password provided does not match the user's actual password.
     * @throws ConfirmPasswordIsNotEqual if the new password and the confirmation password do not match.
     */
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestBody final PasswordUpdateRequestDTO request
    ) throws InvalidPassword, ConfirmPasswordIsNotEqual {
        // Retrieve the currently authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Call the service to update the password
        service.updatePassword(
                currentUser,
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getConfirmPassword());

        // Return a successful response
        return ResponseEntity.ok("Password updated successfully.");
    }


    /**
     * Handles GET requests to retrieve a user's profile by their ID.
     *
     * @param id the ID of the user whose profile is being retrieved.
     * @return a {@link ResponseEntity} containing the user's profile data.
     * @throws NotFound if no user is found with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable final Long id) throws NotFound {
        return ResponseEntity.ok(service.getUserProfile(id));
    }

    /**
     * Deletes the currently authenticated user.
     *
     * @return {@link ResponseEntity} indicating the result of the deletion
     * @throws NotFound if an error occurs during deletion
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUser() throws NotFound {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        facade.deleteUserById(currentUser);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
