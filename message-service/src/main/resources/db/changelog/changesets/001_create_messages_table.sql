-- changesets/001_create_messages_table.sql

CREATE TABLE messages
(
    id                SERIAL PRIMARY KEY,
    encrypted_message TEXT        NOT NULL,
    encryption_type   VARCHAR(10) NOT NULL,
    user_id           INT         NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
