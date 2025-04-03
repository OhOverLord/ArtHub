package org.arthub.backend.exception;

public class EmptyFile extends Exception implements CustomException {

    /**
     * Returns the message indicating that the requested resource was not found.
     *
     * @return the message "Not Found"
     */
    @Override
    public String getMessage() {
        return "Empty file";
    }
}
