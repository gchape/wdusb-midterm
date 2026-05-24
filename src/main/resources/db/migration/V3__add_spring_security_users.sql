CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    PRIMARY
    KEY
    DEFAULT
    nextval
(
    'users_id_seq'
),
    username VARCHAR
(
    100
) NOT NULL UNIQUE,
    password_hash VARCHAR
(
    255
) NOT NULL,
    role VARCHAR
(
    20
) NOT NULL DEFAULT 'USER'
    CHECK
(
    role
    IN
(
    'USER',
    'ADMIN'
)),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
    );

INSERT INTO users (username, password_hash, role)
VALUES ('admin', '$2a$10$bOEnYYFOQGf57ZGv4JOtRuP7LO3LbupFxEO1WpIKo7zP0fBkEoBeq', 'ADMIN'),
       ('user', '$2a$10$ctJTWu83HUDI04ZkGBQXUefXnh/KzA7Z3kfRdj8.bv7oy3MqFP64G',
        'USER') ON CONFLICT (username) DO NOTHING;