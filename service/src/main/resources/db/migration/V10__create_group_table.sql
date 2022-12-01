CREATE TABLE group_configuration (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uuid varchar(36) not null,
  group_name varchar(50) NOT NULL,
  display_order int(3) not null,
  PRIMARY KEY (id)
)
;
