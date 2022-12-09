CREATE TABLE mail_subscription (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uuid varchar(36) NOT NULL,
  email varchar(100) not null,
  announcements smallint(1) not null,
  confirmed smallint(1) not null,
  confirm_identifier varchar(36) not null,
  PRIMARY KEY (id),
  UNIQUE INDEX uuid (uuid),
  UNIQUE INDEX email (email),
  UNIQUE INDEX confirm_identifier (confirm_identifier)
)
;
