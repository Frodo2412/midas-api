CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    email         TEXT UNIQUE,
    first_name    TEXT,
    last_name     TEXT,
    password_hash TEXT
)
