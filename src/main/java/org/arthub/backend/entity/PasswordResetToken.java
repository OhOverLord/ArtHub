package org.arthub.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Entity representing a token for resetting user passwords.
 * This token is associated with a user and has an expiration time.
 */
@Entity
@Table(name = "passwordResetToken")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PasswordResetToken {

    /**
     * The expiration time for the token, in minutes.
     */
    private static final int EXPIRATION = 60;

    /**
     * Unique identifier for the token entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The token string used for password reset verification.
     */
    private String token;

    /**
     * The user associated with this token.
     */
    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    /**
     * The date and time when the token was created.
     */
    private LocalDateTime creationDate;

    /**
     * Indicates whether the token has been used.
     */
    @Builder.Default
    private Boolean isUsed = Boolean.FALSE;

    /**
     * Checks if the token is still valid.
     *
     * @return {@code true} if the token is not expired and has not been used; {@code false} otherwise.
     */
    public Boolean isValid() {
        Duration diff = Duration.between(creationDate, LocalDateTime.now());
        return diff.toMinutes() < EXPIRATION && !isUsed;
    }
}

