CREATE TABLE stocks
(
    id                 UUID         NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    symbol             VARCHAR(10),
    company_name       VARCHAR(255) NOT NULL,
    sector             VARCHAR(50),
    current_price      DECIMAL(19, 4),
    day_change_percent DECIMAL(10, 4),
    previous_close     DECIMAL(10, 4),
    market_cap         DECIMAL(19, 4),
    CONSTRAINT pk_stocks PRIMARY KEY (id)
);

ALTER TABLE stocks
    ADD CONSTRAINT uc_stocks_symbol UNIQUE (symbol);