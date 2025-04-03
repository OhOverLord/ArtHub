package org.arthub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling password reset requests.
 * This DTO contains the token for verification and the new password details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestDTO {

    /**
     * The token used to verify the password reset request.
     */
    private String token;

    /**
     * The new password that the user wants to set.
     */
    private String password;

    /**
     * Confirmation of the new password to ensure correctness.
     */
    private String confirmPassword;
}
