CREATE TABLE loan
(
    id                  UUID           NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    user_id             UUID           NOT NULL,
    loan_name           VARCHAR(255)   NOT NULL,
    loan_type           VARCHAR(255)   NOT NULL,
    lender_name         VARCHAR(255)   NOT NULL,
    principal_amount    DECIMAL(19, 2) NOT NULL,
    outstanding_balance DECIMAL(19, 2) NOT NULL,
    interest_rate       DECIMAL(5, 2)  NOT NULL,
    emi_amount          DECIMAL(19, 2) NOT NULL,
    start_date          date           NOT NULL,
    end_date            date           NOT NULL,
    status              VARCHAR(255)   NOT NULL,
    CONSTRAINT pk_loan PRIMARY KEY (id)
);

ALTER TABLE loan
    ADD CONSTRAINT FK_LOAN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);