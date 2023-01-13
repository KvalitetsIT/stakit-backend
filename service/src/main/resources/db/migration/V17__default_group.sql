insert into group_configuration(uuid, name, display_order) values(uuid(), 'Default', 10);
update service_configuration set group_configuration_id = (select min(id) from group_configuration where name = 'Default');
alter table service_configuration modify group_configuration_id bigint(20) not null;
