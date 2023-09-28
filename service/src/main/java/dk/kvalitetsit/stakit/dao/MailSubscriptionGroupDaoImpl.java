package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    @Override
    public void deleteByUuid(UUID uuid) {
        var sql = "delete from mail_subscription_group where mail_subscription_id = (select id from mail_subscription where uuid = :uuid)";

        template.update(sql, Collections.singletonMap("uuid", uuid.toString()));
    }

    @Override
    public List<SubscriptionGroupEntity> getSubscriptions() {
        String sql = "SELECT ms.uuid as sub_uuid, ms.email, ms.announcements, gc.uuid as group_uuid " +
                "FROM mail_subscription ms " +
                "LEFT JOIN mail_subscription_group msg ON ms.id = msg.mail_subscription_id " +
                "LEFT JOIN group_configuration gc ON msg.group_configuration_id = gc.id";

        return  template.query(sql, DataClassRowMapper.newInstance(SubscriptionGroupEntity.class));
    }

    @Override
    public List<SubscriptionGroupEntity> getSubscriptionByUuid(UUID uuid) {
        String sql = "SELECT ms.uuid as sub_uuid, ms.email, ms.announcements, gc.uuid as group_uuid " +
                "FROM mail_subscription ms " +
                "LEFT JOIN mail_subscription_group msg ON ms.id = msg.mail_subscription_id " +
                "LEFT JOIN group_configuration gc ON msg.group_configuration_id = gc.id " +
                "WHERE ms.uuid = :uuid ";

        return template.query(sql, Collections.singletonMap("uuid", uuid.toString()), DataClassRowMapper.newInstance(SubscriptionGroupEntity.class));
    }

    @Override
    public void deleteByGroupUuid(UUID uuid) {
        var sql = "delete from mail_subscription_group where group_configuration_id = (select id from group_configuration g where g.uuid = :uuid)";

        template.update(sql, Collections.singletonMap("uuid", uuid.toString()));
    }
}
