CREATE TABLE login_policy (
    id UUID PRIMARY KEY,
    max_attempts INT NOT NULL,
    lock_time_millis BIGINT NOT NULL
);


