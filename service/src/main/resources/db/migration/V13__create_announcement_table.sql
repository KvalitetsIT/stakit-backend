CREATE TABLE announcement (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uuid varchar(36) NOT NULL,
  from_datetime datetime not null,
  to_datetime datetime null,
  subject varchar(100),
  message text,
  PRIMARY KEY (id),
  UNIQUE INDEX uuid (uuid),
  INDEX to_datetime (to_datetime)
)
;
