CREATE TABLE status (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  status_configuration_id bigint(20) NOT NULL,
  status varchar(6) not null,
  status_time datetime(6) not null,
  message varchar(250),
  PRIMARY KEY (id),
  FOREIGN KEY (status_configuration_id) references status_configuration(id),
  INDEX status_configuration_status_time (status_configuration_id, status_time)
)
;
