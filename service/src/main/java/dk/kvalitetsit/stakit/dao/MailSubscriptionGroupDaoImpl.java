package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;

public class MailSubscriptionGroupDaoImpl implements MailSubscriptionGroupDao {
    private final NamedParameterJdbcTemplate template;

    public MailSubscriptionGroupDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void insert(MailSubscriptionGroupsEntity mailSubscriptionGroupsEntity) {
        var sql = "insert into mail_subscription_group(mail_subscription_id, group_configuration_id) values(:mail_subscription_id, :group_configuration_id)";

        var parameters = new MapSqlParameterSource()
                .addValue("mail_subscription_id", mailSubscriptionGroupsEntity.mailSubscriptionId())
                .addValue("group_configuration_id", mailSubscriptionGroupsEntity.groupConfigurationId());

        template.update(sql, parameters);
    }

    @Override
    public void deleteByEmail(String email) {
        var sql = "delete from mail_subscription_group where mail_subscription_id = (select id from mail_subscription where email = :email)";

        template.update(sql, Collections.singletonMap("email", email));
    }
}
