CREATE TABLE stock_holdings
(
    id                   UUID    NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    portfolio_id         UUID,
    stock_id             UUID,
    quantity             INTEGER NOT NULL,
    average_cost_basis   DECIMAL(19, 4),
    current_value        DECIMAL(19, 4),
    day_change_percent   DECIMAL(10, 4),
    total_return_percent DECIMAL(10, 4),
    CONSTRAINT pk_stock_holdings PRIMARY KEY (id)
);

ALTER TABLE stock_holdings
    ADD CONSTRAINT FK_STOCK_HOLDINGS_ON_PORTFOLIO FOREIGN KEY (portfolio_id) REFERENCES portfolios (id);

ALTER TABLE stock_holdings
    ADD CONSTRAINT FK_STOCK_HOLDINGS_ON_STOCK FOREIGN KEY (stock_id) REFERENCES stocks (id);