package org.arthub.backend.exception;

/**
 * Exception thrown when the confirmation password does not match the original password.
 */
public class ConfirmPasswordIsNotEqual extends Exception implements CustomException {

    /**
     * Returns the error message associated with this exception.
     *
     * @return A string indicating that the confirm password does not match.
     */
    @Override
    public String getMessage() {
        return "Confirm password is not equal";
    }
}
