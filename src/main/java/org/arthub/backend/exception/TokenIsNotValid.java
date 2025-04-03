package org.arthub.backend.exception;

/**
 * Exception thrown when a provided token is invalid.
 */
public class TokenIsNotValid extends Exception implements CustomException {

    /**
     * Returns the error message associated with this exception.
     *
     * @return A string indicating that the token is not valid.
     */
    @Override
    public String getMessage() {
        return "Token is not valid";
    }
}
