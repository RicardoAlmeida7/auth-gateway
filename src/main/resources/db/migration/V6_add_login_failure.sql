ALTER TABLE users
ADD COLUMN failed_login_attempts INT DEFAULT 0 NOT NULL;

ALTER TABLE users
ADD COLUMN last_failed_login_time BIGINT DEFAULT 0 NOT NULL;
