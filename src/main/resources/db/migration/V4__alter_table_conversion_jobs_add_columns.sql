ALTER TABLE conversion_jobs
ADD COLUMN user_id BIGINT NOT NULL,
ADD CONSTRAINT fk_conversion_jobs_users
FOREIGN KEY (user_id) REFERENCES users (id);