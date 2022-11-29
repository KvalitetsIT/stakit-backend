CREATE TABLE status_configuration (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  service_name varchar(100) not null,
  service varchar(100) not null,
  ignore_service_name tinyint(1) not null default 0,
  PRIMARY KEY (id),
  constraint service UNIQUE (service)
)
;
