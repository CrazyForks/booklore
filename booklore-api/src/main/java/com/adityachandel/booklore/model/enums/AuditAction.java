package com.adityachandel.booklore.model.enums;

/**
 * Defines the types of actions that can be tracked in the audit trail.
 */
public enum AuditAction {
    // Authentication events
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    PASSWORD_CHANGED,
    PERMISSIONS_CHANGED,

    // Library operations
    LIBRARY_CREATED,
    LIBRARY_UPDATED,
    LIBRARY_DELETED,
    LIBRARY_SCANNED,

    // Book operations
    BOOK_UPLOADED,
    BOOK_DELETED,
    BOOK_SENT,
    METADATA_UPDATED,

    // Shelf operations
    SHELF_CREATED,
    SHELF_UPDATED,
    SHELF_DELETED,

    // Settings
    SETTINGS_UPDATED,
    OIDC_CONFIG_CHANGED,
    NAMING_PATTERN_CHANGED,

    // Tasks
    TASK_EXECUTED,

    // Rate limiting
    LOGIN_RATE_LIMITED,
    REFRESH_RATE_LIMITED,

    // Email
    EMAIL_PROVIDER_CREATED,
    EMAIL_PROVIDER_UPDATED,
    EMAIL_PROVIDER_DELETED,

    // OPDS
    OPDS_USER_CREATED,
    OPDS_USER_DELETED,
    OPDS_USER_UPDATED
}
