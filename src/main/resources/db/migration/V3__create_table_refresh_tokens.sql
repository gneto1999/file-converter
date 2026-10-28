CREATE TABLE refresh_tokens (
    id SERIAL PRIMARY KEY,
    hash_token TEXT NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);