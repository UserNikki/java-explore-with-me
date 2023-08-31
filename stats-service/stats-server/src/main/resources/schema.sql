DROP TABLE IF EXISTS stat CASCADE;

CREATE TABLE IF NOT EXISTS stat
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app VARCHAR,
    uri VARCHAR,
    ip VARCHAR,
    created TIMESTAMP
);