alter table "AccessManagement"
add permissions integer not null default 0;

alter table "AccessManagement"
alter column permissions drop default;