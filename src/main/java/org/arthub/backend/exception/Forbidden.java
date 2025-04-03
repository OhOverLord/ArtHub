/**
 * This package contains custom exception classes for the Arthub backend application.
 * <p>
 * These exceptions are used to handle specific error cases within the application, providing
 * meaningful feedback to the user when something goes wrong.
 * </p>
 */
package org.arthub.backend.exception;

/**
 * Exception class to indicate a forbidden action.
 * <p>
 * This exception is thrown when a user attempts to perform an action that they do not have permission to execute.
 * </p>
 *
 * @since 1.0
 */
public class Forbidden extends Exception implements CustomException {

    /**
     * Returns the message indicating that the action is forbidden.
     *
     * @return the message "Forbidden"
     */
    @Override
    public String getMessage() {
        return "Forbidden";
    }
}
