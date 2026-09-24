CREATE TABLE USERS (
    id          	BIGSERIAL        PRIMARY KEY,
    public_id   	UUID             DEFAULT uuidv7() NOT NULL UNIQUE,
    first_name      VARCHAR(80)      NOT NULL,
    last_name       VARCHAR(120)     NOT NULL,
    email           VARCHAR(150)     NOT NULL UNIQUE,
    password_hash   VARCHAR(255)     NOT NULL,
    role            VARCHAR(5)       NOT NULL,
    created_at      TIMESTAMPTZ 	 NOT NULL,
    updated_at      TIMESTAMPTZ		 NOT NULL
);