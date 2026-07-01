CREATE TABLE portfolios
(
    id                   UUID         NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    user_id              UUID,
    name                 VARCHAR(255) NOT NULL,
    total_invested       DECIMAL(19, 4),
    current_value        DECIMAL(19, 4),
    total_return_percent DECIMAL(10, 4),
    risk_level           VARCHAR(20),
    CONSTRAINT pk_portfolios PRIMARY KEY (id)
);

ALTER TABLE portfolios
    ADD CONSTRAINT FK_PORTFOLIOS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);