CREATE TABLE budgets
(
    id            UUID           NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    user_id       UUID           NOT NULL,
    name          VARCHAR(100)   NOT NULL,
    category      VARCHAR(255)   NOT NULL,
    target_amount DECIMAL(19, 4) NOT NULL,
    current_spent DECIMAL(19, 4) NOT NULL,
    period VARCHAR (255) NOT NULL,
    start_date    date           NOT NULL,
    end_date      date           NOT NULL,
    CONSTRAINT pk_budgets PRIMARY KEY (id)
);

ALTER TABLE budgets
    ADD CONSTRAINT uk_budget_user_category_period UNIQUE (user_id, category, start_date, end_date);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);