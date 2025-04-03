/**
 * This package contains custom exception classes and interfaces for the Arthub backend application.
 * <p>
 * These exceptions and interfaces are used to handle specific error cases within the application, providing
 * meaningful feedback to the user when something goes wrong.
 * </p>
 */
package org.arthub.backend.exception;

/**
 * Interface for custom exceptions in the Arthub backend application.
 * <p>
 * This interface can be implemented by custom exception classes to provide a standard way
 * of retrieving the exception message.
 * </p>
 *
 * @since 1.0
 */
public interface CustomException {

    /**
     * Returns the message of the exception.
     *
     * @return the detail message of the exception
     */
    String getMessage();
}
