CREATE TABLE service_configuration (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uuid varchar(36) not null,
  name varchar(100) not null,
  service varchar(100) not null,
  ignore_service_name tinyint(1) not null default 0,
  group_configuration_id bigint(20) null,
  PRIMARY KEY (id),
  constraint service UNIQUE (service),
  FOREIGN KEY (group_configuration_id) references group_configuration(id)
)
;
