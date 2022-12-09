CREATE TABLE mail_subscription_group (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  mail_subscription_id bigint(20) not null,
  group_configuration_id bigint(20) not null,
  PRIMARY KEY (id),
  FOREIGN KEY (mail_subscription_id) references mail_subscription(id),
  FOREIGN KEY (group_configuration_id) references group_configuration(id)
)
;
