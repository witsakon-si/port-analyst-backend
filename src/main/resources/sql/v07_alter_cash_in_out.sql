alter table cash_in_out add remark varchar;
alter table cash_in_out add dividend boolean not null DEFAULT false;
