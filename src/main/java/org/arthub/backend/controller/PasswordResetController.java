package org.arthub.backend.controller;

import lombok.RequiredArgsConstructor;
import org.arthub.backend.dto.PasswordResetRequestDTO;
import org.arthub.backend.exception.ConfirmPasswordIsNotEqual;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.exception.TokenIsNotValid;
import org.arthub.backend.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling password reset functionality.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class PasswordResetController {

    /**
     * Service for managing password reset operations.
     */
    private final PasswordResetService passwordResetService;

    /**
     * Endpoint to request a password reset token.
     * Sends an email with the token to the provided email address.
     *
     * @param email the email address of the user requesting a password reset.
     * @return a {@link ResponseEntity} indicating the request status.
     * @throws Exception if the email does not exist or an error occurs while processing the request.
     */
    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam final String email) throws NotFound {
        passwordResetService.resetPasswordRequest(email);
        return ResponseEntity.ok("Password reset token sent to your email.");
    }

    /**
     * Endpoint to reset the user's password.
     * Validates the reset token and updates the user's password if valid.
     *
     * @param request a {@link PasswordResetRequestDTO} containing the reset token, new password,
     * and password confirmation.
     * @return a {@link ResponseEntity} indicating the request status.
     * @throws Exception if the token is invalid, expired, or the passwords do not match.
     */
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody final PasswordResetRequestDTO request)
            throws NotFound, TokenIsNotValid, ConfirmPasswordIsNotEqual {
        passwordResetService.resetPassword(request.getToken(), request.getPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("Password has been successfully reset.");
    }
}

