-- Audit trail table for tracking important system events
-- Records who did what, when, and from where

CREATE TABLE IF NOT EXISTS audit_log (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_id            BIGINT        NULL,
    actor_username      VARCHAR(100)  NOT NULL,
    event_type          VARCHAR(50)   NOT NULL,
    target_entity_type  VARCHAR(50)   NULL,
    target_entity_id    BIGINT        NULL,
    description         VARCHAR(2000) NOT NULL,
    client_ip           VARCHAR(45)   NULL,
    country_code        CHAR(2)       NULL,
    event_timestamp     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata            TEXT          NULL
);

-- Indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_log (event_timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_actor ON audit_log (actor_id);
CREATE INDEX IF NOT EXISTS idx_audit_event_type ON audit_log (event_type);
CREATE INDEX IF NOT EXISTS idx_audit_actor_username ON audit_log (actor_username);
