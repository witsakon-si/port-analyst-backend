DROP TABLE IF EXISTS "main"."user";
-- auto-generated definition
create table user
(
    id          bigint not null
        primary key,
    email       varchar(255),
    firstname       varchar(255),
    lastname       varchar(255),
    password       varchar(255)
);
