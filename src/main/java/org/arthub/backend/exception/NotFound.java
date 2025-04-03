/**
 * This package contains custom exception classes for the Arthub backend application.
 * <p>
 * These exceptions are used to handle specific error cases within the application, providing
 * meaningful feedback to the user when something goes wrong.
 * </p>
 */
package org.arthub.backend.exception;

/**
 * Exception class to indicate that a requested resource was not found.
 * <p>
 * This exception is thrown when a requested resource (e.g., a database entity) cannot be found.
 * </p>
 *
 * @since 1.0
 */
public class NotFound extends Exception implements CustomException {

    /**
     * Returns the message indicating that the requested resource was not found.
     *
     * @return the message "Not Found"
     */
    @Override
    public String getMessage() {
        return "Not Found";
    }
}
