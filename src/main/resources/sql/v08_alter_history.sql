alter table history add dividend boolean not null DEFAULT false;
alter table history add cash_in_out_id bigint;
alter table history add interest boolean not null DEFAULT false;
alter table history add order_match text not null DEFAULT '-';
