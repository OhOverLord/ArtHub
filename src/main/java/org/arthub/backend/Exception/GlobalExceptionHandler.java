package org.arthub.backend.Exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the Arthub backend application.
 * <p>
 * This class handles various exceptions that occur throughout the application and returns appropriate
 * HTTP responses and messages based on the type of exception.
 * </p>
 *
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions related to database access errors.
     *
     * @param ex the exception to handle
     * @return a response entity with the error message and HTTP 500 status
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<String> handleDataAccessException(final DataAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles forbidden access exceptions.
     *
     * @param ex the exception to handle
     * @return a response entity with the error message and HTTP 403 status
     */
    @ExceptionHandler(Forbidden.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<String> handleForbiddenException(final Forbidden ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles not found exceptions.
     *
     * @param ex the exception to handle
     * @return a response entity with the error message and HTTP 404 status
     */
    @ExceptionHandler(NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<String> handleNotFoundException(final NotFound ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles entity already exists exceptions.
     *
     * @param ex the exception to handle
     * @return a response entity with the error message and HTTP 409 status
     */
    @ExceptionHandler(AlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ResponseEntity<String> handleAlreadyExistsException(final AlreadyExists ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handles generic exceptions related to security.
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and the appropriate HTTP status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleSecurityException(final Exception exception) {
        exception.printStackTrace();
        return switch (exception.getClass().getSimpleName()) {
            case "UsernameNotFoundException" -> handleUsernameNotFoundException(exception);
            case "BadCredentialsException" -> handleBadCredentialsException(exception);
            case "AccountStatusException" -> handleAccountStatusException(exception);
            case "AccessDeniedException" -> handleAccessDeniedException(exception);
            case "SignatureException" -> handleSignatureException(exception);
            case "ExpiredJwtException" -> handleExpiredJwtException(exception);
            default -> handleUnknownException(exception);
        };
    }

    /**
     * Handles username not found exceptions.
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and HTTP 401 status
     */
    private ResponseEntity<ProblemDetail> handleUsernameNotFoundException(final Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(401), exception.getMessage()
        );
        errorDetail.setProperty("description", "User not found");
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles bad credentials exceptions.
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and HTTP 401 status
     */
    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<ProblemDetail> handleBadCredentialsException(final Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(401), exception.getMessage()
        );
        errorDetail.setProperty("description", "The username or password is incorrect");
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles account status exceptions (e.g., account locked).
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and HTTP 403 status
     */
    private ResponseEntity<ProblemDetail> handleAccountStatusException(final Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(403), exception.getMessage()
        );
        errorDetail.setProperty("description", "The account is locked");
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles access denied exceptions.
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and HTTP 403 status
     */
    private ResponseEntity<ProblemDetail> handleAccessDeniedException(final Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(403), exception.getMessage()
        );
        errorDetail.setProperty("description", "You are not authorized to access this resource");
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles JWT signature exceptions.
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and HTTP 403 status
     */
    private ResponseEntity<ProblemDetail> handleSignatureException(final Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(403), exception.getMessage()
        );
        errorDetail.setProperty("description", "The JWT signature is invalid");
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles expired JWT exceptions.
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and HTTP 403 status
     */
    private ResponseEntity<ProblemDetail> handleExpiredJwtException(final Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(403), exception.getMessage()
        );
        errorDetail.setProperty("description", "The JWT token has expired");
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles unknown exceptions that do not match any predefined exception type.
     *
     * @param exception the exception to handle
     * @return a response entity with problem details and HTTP 500 status
     */
    private ResponseEntity<ProblemDetail> handleUnknownException(final Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(500), exception.getMessage()
        );
        errorDetail.setProperty("description", "Unknown internal server error.");
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
}
