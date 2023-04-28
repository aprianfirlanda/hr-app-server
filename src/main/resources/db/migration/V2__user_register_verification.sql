CREATE TABLE verification_token
(
    id          UUID                        NOT NULL,
    expire_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id     UUID,
    CONSTRAINT pk_verification_token PRIMARY KEY (id)
);

ALTER TABLE users
    ADD enabled BOOLEAN;

UPDATE users
set enabled = true
where enabled is null;

ALTER TABLE users
    ALTER COLUMN enabled SET NOT NULL;

ALTER TABLE verification_token
    ADD CONSTRAINT FK_VERIFICATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);