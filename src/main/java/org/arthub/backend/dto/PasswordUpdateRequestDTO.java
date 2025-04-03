package org.arthub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling password update requests.
 * This DTO contains the current password for verification and the new password details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateRequestDTO {

    /**
     * The current password of the user, used for authentication before updating.
     */
    private String currentPassword;

    /**
     * The new password that the user wants to set.
     */
    private String newPassword;

    /**
     * Confirmation of the new password to ensure correctness.
     */
    private String confirmPassword;
}

