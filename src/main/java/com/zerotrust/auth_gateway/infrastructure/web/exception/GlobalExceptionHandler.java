package com.zerotrust.auth_gateway.infrastructure.web.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zerotrust.auth_gateway.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidPassword(InvalidPasswordException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Object> handleEmailException(InvalidEmailException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<Object> handleUsernameException(InvalidUsernameException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Object> handleTokenExpiredException(TokenExpiredException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Token has expired.");
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Object> handleRoleException(InvalidRoleException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(FirstAccessPasswordRequiredException.class)
    public ResponseEntity<Object> handleFirstAccessPasswordRequiredException(FirstAccessPasswordRequiredException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "FIRST_TIME_LOGIN", ex.getMessage());
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<Object> handlePasswordResetException(PasswordResetException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<Object> handleJWTDecodeException(JWTDecodeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String specificErrorCode, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", specificErrorCode);
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        return this.buildResponse(status, status.getReasonPhrase(), message);
    }
}
