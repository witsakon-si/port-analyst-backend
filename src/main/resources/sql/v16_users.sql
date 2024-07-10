DROP TABLE IF EXISTS "main"."users";
-- auto-generated definition
create table users
(
    id          bigint not null
        primary key,
    email       varchar(255),
    firstname       varchar(255),
    lastname       varchar(255),
    password       varchar(255)
);
