DROP TABLE IF EXISTS "main"."price_alert";
-- auto-generated definition
create table price_alert
(
    id          bigint  not null
        primary key,
    active      boolean not null,
    created_at  datetime,
    updated_at  datetime,
    version     integer not null,
    condition   integer,
    frequency   integer,
    last_notice datetime,
    note        varchar(255),
    price       numeric(19, 2),
    symbol      varchar(255)
);

