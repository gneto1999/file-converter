CREATE TABLE CONVERSION_JOBS (
    id          	     BIGSERIAL        PRIMARY KEY,
    public_id   	     UUID             DEFAULT uuidv7() NOT NULL UNIQUE,
    original_filename    VARCHAR(260)     NOT NULL,
    input_format         VARCHAR(5)       NOT NULL,
    output_format        VARCHAR(5)       NOT NULL,
    status               VARCHAR(10)      NOT NULL,
    input_path           VARCHAR(500)     NOT NULL,
    output_path          VARCHAR(500),
    error_message        TEXT,
    created_at           TIMESTAMPTZ 	  NOT NULL,
    started_at           TIMESTAMPTZ,
    completed_at         TIMESTAMPTZ
);