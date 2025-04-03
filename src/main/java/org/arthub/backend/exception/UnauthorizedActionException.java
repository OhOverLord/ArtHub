package org.arthub.backend.exception;

/**
 * Exception thrown when a user attempts an unauthorized action.
 * <p>
 * This exception is used to indicate that a user does not have the necessary permissions
 * to perform the requested action.
 * </p>
 */
public class UnauthorizedActionException extends Exception implements CustomException {

    /**
     * Retrieves the error message for this exception.
     *
     * @return the message associated with the exception
     */
    @Override
    public String getMessage() {
        return "There is no authorization to perform this action.";
    }
}
