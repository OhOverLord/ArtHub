package org.arthub.backend.responses;

import lombok.Data;

/**
 * Represents the response returned after a successful login.
 * This class contains the authentication token and the duration
 * for which the token is valid.
 */
@Data
public class LoginResponse {

    /** The authentication token issued upon successful login. */
    private String token;

    /** The duration in seconds until the token expires. */
    private long expiresIn;

    /**
     * Returns a string representation of the LoginResponse object.
     * This method formats the output to include the token and expiration duration.
     *
     * @return a string representation of the LoginResponse
     */
    @Override
    public String toString() {
        return "LoginResponse{"
                + "token='" + token + '\''
                + ", expiresIn=" + expiresIn
                + '}';
    }
}
