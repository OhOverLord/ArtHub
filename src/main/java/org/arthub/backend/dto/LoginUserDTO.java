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
 * Data Transfer Object for user login operations.
 * <p>
 * This DTO is used to transfer login credentials, including the username and password.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDTO {

    /**
     * The username of the user attempting to log in.
     */
    private String username;

    /**
     * The password of the user attempting to log in.
     */
    private String password;
}
