package com.adityachandel.booklore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for API errors with HTTP status.
 */
@Getter
public class APIException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public APIException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
