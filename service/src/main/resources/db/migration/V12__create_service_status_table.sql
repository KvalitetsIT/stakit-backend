CREATE TABLE service_status (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  service_configuration_id bigint(20) NOT NULL,
  status varchar(6) not null,
  status_time datetime(6) not null,
  message varchar(250),
  PRIMARY KEY (id),
  FOREIGN KEY (service_configuration_id) references service_configuration(id),
  INDEX service_configuration_status_time (service_configuration_id, status_time)
)
;
