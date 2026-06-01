CREATE TABLE users
(
    id                    UUID         NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    first_name            VARCHAR(255) NOT NULL,
    last_name             VARCHAR(255) NOT NULL,
    password              VARCHAR(255) NOT NULL,
    role                  VARCHAR(255),
    email_verified        BOOLEAN      NOT NULL,
    mfa_enabled           BOOLEAN      NOT NULL,
    mfa_secret            VARCHAR(255),
    avatar_url            VARCHAR(255),
    last_login_at         TIMESTAMP WITHOUT TIME ZONE,
    account_non_locked    BOOLEAN      NOT NULL,
    failed_login_attempts INTEGER      NOT NULL,
    lockout_time          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);