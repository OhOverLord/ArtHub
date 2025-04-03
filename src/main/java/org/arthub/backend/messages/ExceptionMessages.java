package org.arthub.backend.messages;

import lombok.Getter;

/**
 * Enumeration of standardized log messages used in the Arthub backend application.
 * <p>
 * This enum provides predefined log messages to be used across different parts
 * of the application for logging events consistently and clearly.
 * Each constant represents a specific log message with placeholders (e.g., "{}")
 * that can be replaced with contextual information.
 * </p>
 *
 * @since 1.0
 */
@Getter
public enum ExceptionMessages {
    /** Attempting to retrieve a folder by its ID. */
    SERVICE_UNAVAIABLE("Association Service is unavailable");

    /**
     * Template message to logging.
     */
    private final String message;

    /**
     * Constructor for LogMessages.
     *
     * @param message the log message template associated with the enum constant
     */
    ExceptionMessages(final String message) {
        this.message = message;
    }
}
