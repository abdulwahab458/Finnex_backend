CREATE TABLE transactions
(
    id               UUID           NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    account_id       UUID,
    transaction_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    amount           DECIMAL(19, 4) NOT NULL,
    type             VARCHAR(255)   NOT NULL,
    catetory         VARCHAR(255)   NOT NULL,
    status           VARCHAR(255)   NOT NULL,
    notes            VARCHAR(1000),
    merchant_name    VARCHAR(255),
    attachment_url   VARCHAR(255),
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);