CREATE TABLE goals
(
    id             UUID           NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    user_id        UUID           NOT NULL,
    name           VARCHAR(100)   NOT NULL,
    target_amount  DECIMAL(19, 4) NOT NULL,
    current_amount DECIMAL(19, 4) NOT NULL,
    status         VARCHAR(30)    NOT NULL,
    target_date    date           NOT NULL,
    category       VARCHAR(30)    NOT NULL,
    CONSTRAINT pk_goals PRIMARY KEY (id)
);

ALTER TABLE goals
    ADD CONSTRAINT uk_goal_user_name UNIQUE (user_id, name);

ALTER TABLE goals
    ADD CONSTRAINT FK_GOALS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);