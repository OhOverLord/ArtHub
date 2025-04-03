package org.arthub.backend.repository;

import org.arthub.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link PasswordResetToken} entities.
 * Provides methods to interact with the database for password reset tokens.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Finds a {@link PasswordResetToken} by its token string.
     *
     * @param token the token string to search for.
     * @return an {@link Optional} containing the found token, or empty if no token is found.
     */
    Optional<PasswordResetToken> findByToken(String token);
}
