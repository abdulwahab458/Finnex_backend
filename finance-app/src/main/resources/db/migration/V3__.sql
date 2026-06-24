CREATE TABLE accounts
(
    id                UUID         NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    user_id           UUID,
    account_number    VARCHAR(255),
    account_type      VARCHAR(255),
    account_name      VARCHAR(255) NOT NULL,
    balance           DECIMAL(19, 4),
    available_balance DECIMAL(19, 4),
    currency          VARCHAR(3),
    active            BOOLEAN      NOT NULL,
    opened_date       date,
    closed_date       date,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_accountnumber UNIQUE (account_number);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);