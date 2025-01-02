CREATE TABLE user_roles
(
    user_id INT REFERENCES users (id) ON DELETE CASCADE,
    roles    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, roles)
);
