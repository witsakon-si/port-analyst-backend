DROP TABLE IF EXISTS "main"."note";
-- auto-generated definition
create table note
(
    name       varchar(255) not null
        primary key,
    note       varchar(255),
    updated_at datetime
);
