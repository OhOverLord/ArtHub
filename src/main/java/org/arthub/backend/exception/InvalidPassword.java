package org.arthub.backend.exception;

/**
 * Exception thrown when an invalid password is encountered during validation.
 */
public class InvalidPassword extends Exception implements CustomException {

  /**
   * Returns the error message associated with this exception.
   *
   * @return A string indicating that the password validation failed.
   */
  @Override
  public String getMessage() {
    return "Old password is incorrect";
  }
}

