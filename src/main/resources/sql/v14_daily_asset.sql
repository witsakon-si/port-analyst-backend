DROP TABLE IF EXISTS "main"."daily_asset";
-- auto-generated definition
create table daily_asset
(
    id          bigint not null
        primary key,
    cost        numeric(19, 2),
    created_at  datetime,
    date        datetime,
    name        varchar(255),
    net_balance numeric(19, 2),
    profit_loss numeric(19, 2),
    remark      varchar(255),
    type        varchar(255)
);
