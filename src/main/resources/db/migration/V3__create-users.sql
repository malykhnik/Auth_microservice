CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    role_id  BIGINT              REFERENCES roles (id) ON DELETE SET NULL
);