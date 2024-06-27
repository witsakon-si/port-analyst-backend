DROP TABLE IF EXISTS "main"."daily_sum";
-- auto-generated definition
create table daily_sum
(
    id          bigint not null
        primary key,
    cost        numeric(19, 2),
    created_at  datetime,
    date        datetime,
    net_balance numeric(19, 2),
    percentpl   numeric(19, 2),
    profit_loss numeric(19, 2),
    remark      varchar(255),
    type        varchar(255)
);

