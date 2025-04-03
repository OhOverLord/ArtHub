package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.PasswordResetToken;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.ConfirmPasswordIsNotEqual;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.exception.TokenIsNotValid;
import org.arthub.backend.repository.PasswordResetTokenRepository;
import org.arthub.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing password reset functionality.
 * Handles generation of reset tokens, validation, and updating user passwords.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    /**
     * Encoder for securely storing user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Repository for managing password reset tokens.
     */
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Repository for managing user data.
     */
    private final UserRepository userRepository;

    /**
     * Service for sending email notifications.
     */
    private final EmailService emailService;

    /**
     * Initiates a password reset request by generating a token and sending it to the user's email.
     *
     * @param email the user's email address.
     * @throws NotFound if no user is associated with the provided email.
     */
    @Transactional
    public void resetPasswordRequest(final String email) throws NotFound {
        log.info("Reset password request initiated for email: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("Reset password request failed: No user found with email: {}", email);
            throw new NotFound();
        }

        User user = userOptional.get();
        log.info("User found for reset password request. User ID: {}", user.getId());

        String token = generateToken();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .isUsed(false)
                .creationDate(LocalDateTime.now())
                .build();

        passwordResetTokenRepository.save(passwordResetToken);
        log.info("Password reset token generated and saved for user ID: {}.", user.getId());

        emailService.sendEmail(
                email,
                "arthub.notifications@gmail.com",
                "Your reset password token",
                "<div style=\"border: 1px solid #ccc; padding: 10px; background-color: #f9f9f9;\">\n"
                        + "    <p>Your token to reset password:</p>\n"
                        + "    <pre style=\"white-space: pre-wrap; word-wrap: break-word;\">"
                        + token + "</pre>\n"
                        + "</div>\n",
                true
        );
        log.info("Password reset email sent successfully to: {}", email);
    }


    /**
     * Resets the user's password if the provided token is valid and the new passwords match.
     *
     * @param token           the password reset token.
     * @param password        the new password.
     * @param confirmPassword the confirmation of the new password.
     * @throws NotFound                  if the token does not exist.
     * @throws TokenIsNotValid           if the token is expired or already used.
     * @throws ConfirmPasswordIsNotEqual if the new password and its confirmation do not match.
     */
    @Transactional
    public void resetPassword(
            final String token,
            final String password,
            final String confirmPassword) throws NotFound, TokenIsNotValid, ConfirmPasswordIsNotEqual {
        log.info("Password reset initiated.");

        Optional<PasswordResetToken> passwordResetTokenOptional = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenOptional.isEmpty()) {
            log.warn("Password reset failed: Token not found.");
            throw new NotFound();
        }

        PasswordResetToken passwordResetToken = passwordResetTokenOptional.get();
        log.info("Password reset token found. User ID: {}",
                passwordResetToken.getUser().getId());

        // Validate token
        Boolean isValid = passwordResetToken.isValid();
        if (isValid == null || !isValid) {
            log.warn("Password reset failed: Token is null or invalid.");
            throw new TokenIsNotValid();
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            log.warn("Password reset failed: Password confirmation does not match");
            throw new ConfirmPasswordIsNotEqual();
        }

        User user = passwordResetToken.getUser();
        log.info("Password reset validation successful for User ID: {}. Updating password.", user.getId());

        user.setPassword(passwordEncoder.encode(password));
        passwordResetToken.setIsUsed(Boolean.TRUE);

        userRepository.save(user);
        passwordResetTokenRepository.save(passwordResetToken);

        log.info("Password successfully reset for User ID: {}.", user.getId());
    }


    /**
     * Generates a unique secure token for password reset.
     *
     * @return the generated token as a {@link String}.
     */
    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID())
                .append(UUID.randomUUID()).toString();
    }
}
