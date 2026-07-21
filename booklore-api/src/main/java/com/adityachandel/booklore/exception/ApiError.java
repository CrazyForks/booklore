package com.adityachandel.booklore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Centralized API error codes with HTTP status codes and messages.
 */
@Getter
public enum ApiError {
    // Generic errors
    GENERIC_NOT_FOUND(HttpStatus.NOT_FOUND, "%s"),
    GENERIC_BAD_REQUEST(HttpStatus.BAD_REQUEST, "%s"),
    GENERIC_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "%s"),

    // Book errors
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "Book not found with ID: %d"),
    UNSUPPORTED_BOOK_TYPE(HttpStatus.BAD_REQUEST, "Unsupported book type for viewer settings"),
    INVALID_VIEWER_SETTING(HttpStatus.BAD_REQUEST, "Invalid viewer setting for the book"),
    FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading files from path: %s"),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image not found or not readable"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "Invalid file format: %s"),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "File size exceeds the limit: %d MB"),
    FILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "File already exists"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File not found: %s"),
    FILE_DELETION_DISABLED(HttpStatus.BAD_REQUEST, "File deletion is disabled"),
    UNSUPPORTED_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "%s"),

    // Library errors
    LIBRARY_NOT_FOUND(HttpStatus.NOT_FOUND, "Library not found with ID: %d"),
    INVALID_LIBRARY_PATH(HttpStatus.BAD_REQUEST, "Invalid library path"),
    DIRECTORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create the directory: %s"),
    LIBRARY_PATH_NOT_ACCESSIBLE(HttpStatus.SERVICE_UNAVAILABLE, "Library scan aborted: path not accessible or empty: %s"),

    // Shelf errors
    SHELF_ALREADY_EXISTS(HttpStatus.CONFLICT, "Shelf already exists: %s"),
    SHELF_NOT_FOUND(HttpStatus.NOT_FOUND, "Shelf not found with ID: %d"),
    SHELF_CANNOT_BE_DELETED(HttpStatus.FORBIDDEN, "'%s' shelf can't be deleted"),

    // User errors
    USERNAME_ALREADY_TAKEN(HttpStatus.BAD_REQUEST, "Username already taken: %s"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "User not found: %s"),
    CANNOT_DELETE_ADMIN(HttpStatus.FORBIDDEN, "Admin user cannot be deleted"),
    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "Incorrect current password"),
    PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters long"),
    PASSWORD_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST, "New password cannot be the same as the current password"),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "Invalid credentials"),
    SELF_DELETION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "You cannot delete your own account"),

    // Metadata errors
    METADATA_SOURCE_NOT_IMPLEMENT_OR_DOES_NOT_EXIST(HttpStatus.BAD_REQUEST, "Metadata source not implement or does not exist"),
    FAILED_TO_REGENERATE_COVER(HttpStatus.BAD_REQUEST, "Failed to regenerate cover: %s"),
    NO_COVER_IN_FILE(HttpStatus.BAD_REQUEST, "No embedded cover image found in the audiobook file"),
    METADATA_LOCKED(HttpStatus.FORBIDDEN, "Attempt to update locked metadata"),
    INVALID_REFRESH_TYPE(HttpStatus.BAD_REQUEST, "The refresh type is invalid"),

    // Task errors
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "Scheduled task not found: %s"),
    TASK_ALREADY_RUNNING(HttpStatus.CONFLICT, "Task is already running: %s"),
    SCHEDULE_REFRESH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to schedule metadata refresh job. Error: %s"),
    ANOTHER_METADATA_JOB_RUNNING(HttpStatus.CONFLICT, "A metadata refresh job is currently running. Please wait for it to complete before initiating a new one."),

    // Rate limiting
    RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "Too many failed login attempts. Please try again later."),

    // Permission errors
    FORBIDDEN(HttpStatus.FORBIDDEN, "%s"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "Permission denied: %s"),

    // System errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "%s"),
    CONFLICT(HttpStatus.CONFLICT, "%s"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "%s"),
    INVALID_QUERY_PARAMETERS(HttpStatus.BAD_REQUEST, "Query parameters are required for the search.");

    private final HttpStatus status;
    private final String message;

    ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public APIException createException(Object... details) {
        String formattedMessage = (details.length > 0) ? String.format(message, details) : message;
        return new APIException(formattedMessage, this.status);
    }
}
