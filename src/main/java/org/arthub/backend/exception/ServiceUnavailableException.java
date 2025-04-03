package org.arthub.backend.exception;

/**
 * Exception thrown when an external service is unavailable.
 * <p>
 * This exception is used to signal issues with external services
 * such as unavailability, timeouts, or internal server errors.
 * It encapsulates the original exception for further debugging.
 * </p>
 *
 * @since 1.0
 */
public class ServiceUnavailableException extends RuntimeException {

    /**
     * Constructs a new ServiceUnavailableException with the specified detail message
     * and the cause of the exception.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the underlying cause of the exception
     */
    public ServiceUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
