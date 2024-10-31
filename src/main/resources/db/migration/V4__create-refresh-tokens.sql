CREATE TABLE refresh_tokens
(
    id          SERIAL PRIMARY KEY,
    token       VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP    NOT NULL,
    user_id     BIGINT REFERENCES users (id) ON DELETE CASCADE
);