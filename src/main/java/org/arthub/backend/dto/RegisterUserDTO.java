/**
 * This package contains Data Transfer Object (DTO) classes for the Arthub backend application.
 * <p>
 * These DTOs are used to transfer data between different layers of the application,
 * such as between controllers and services.
 * </p>
 */
package org.arthub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration operations.
 * <p>
 * This DTO is used to transfer data when registering a new user, including the user's
 * email, username, and password.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {

    /**
     * The email address of the user being registered.
     */
    private String email;

    /**
     * The username of the user being registered.
     */
    private String username;

    /**
     * The password of the user being registered.
     */
    private String password;
}
