create table if not exists hits
(
    id
    bigint
    generated
    by
    default as
    identity
    PRIMARY
    KEY
    not
    null,
    app
    varchar
(
    128
),
    uri varchar
(
    128
),
    requester_ip varchar
(
    128
),
    created_on TIMESTAMP WITHOUT TIME ZONE
    );