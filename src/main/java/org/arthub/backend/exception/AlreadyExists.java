/**
 * This package contains custom exception classes for the Arthub backend application.
 * <p>
 * These exceptions are used to handle specific error cases within the application, providing
 * meaningful feedback to the user when something goes wrong.
 * </p>
 */
package org.arthub.backend.exception;

/**
 * Exception class to indicate that an entity already exists.
 * <p>
 * This exception is thrown when an attempt is made to create or add an entity
 * that already exists in the system.
 * </p>
 *
 * @since 1.0
 */
public class AlreadyExists extends Exception implements CustomException {

    /**
     * The custom message to be included when the exception is thrown.
     */
    private final String detailMessage;

    /**
     * Constructs a new AlreadyExists exception with a specified message.
     *
     * @param message the detail message to be included in the exception
     */
    public AlreadyExists(final String message) {
        this.detailMessage = message;
    }

    /**
     * Constructs a new AlreadyExists exception without a custom message.
     */
    public AlreadyExists() {
        detailMessage = "";
    }

    /**
     * Returns the detail message of the exception.
     *
     * @return the detail message, including the phrase "Already Exists" and the custom message
     */
    @Override
    public String getMessage() {
        return "Already Exists " + detailMessage;
    }
}
