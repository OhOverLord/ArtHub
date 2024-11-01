/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to user operations,
 * including account management, adding tags to users, updating user information,
 * and deleting users.
 * </p>
 */
package org.arthub.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Facade.UserFacade;
import org.arthub.backend.Exception.AlreadyExists;
import org.arthub.backend.Service.JwtService;
import org.arthub.backend.Service.UserService;
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
    public ResponseEntity<?> addTags(@RequestBody final List<Long> tagIds) {
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
     * Deletes the currently authenticated user.
     *
     * @return {@link ResponseEntity} indicating the result of the deletion
     * @throws Exception if an error occurs during deletion
     */
    @DeleteMapping
    public ResponseEntity<?> deleteUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        facade.deleteUserById(currentUser);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
