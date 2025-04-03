package org.arthub.backend.exception;

/**
 * Custom exception for email sending errors.
 * <p>
 * This exception is thrown when an error occurs during the email-sending process.
 * It provides a descriptive message about the error and the underlying cause.
 * </p>
 *
 * @since 1.0
 */
public class EmailSendException extends RuntimeException {

    /**
     * Constructs a new EmailSendException with the specified detail message and cause.
     *
     * @param message a descriptive message about the email-sending error
     * @param cause   the underlying cause of the error
     */
    public EmailSendException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
