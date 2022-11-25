CREATE TABLE status (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  service varchar(100) not null,
  status varchar(6) not null,
  status_time datetime(6) not null,
  message varchar(250),
  PRIMARY KEY (id),
  INDEX service_status_time (service, status_time)
)
;
