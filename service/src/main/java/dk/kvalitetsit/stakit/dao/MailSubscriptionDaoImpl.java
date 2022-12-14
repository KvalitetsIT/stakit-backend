package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MailSubscriptionDaoImpl implements MailSubscriptionDao {
    private final NamedParameterJdbcTemplate template;
    public MailSubscriptionDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(MailSubscriptionEntity mailSubscriptionEntity) {
        var sql = "insert into mail_subscription(uuid, announcements, confirmed, confirm_identifier, email) values(:uuid, :announcements, :confirmed, :confirm_identifier, :email)";

        var parameters = new MapSqlParameterSource()
                .addValue("uuid", mailSubscriptionEntity.uuid().toString())
                .addValue("announcements", mailSubscriptionEntity.announcements())
                .addValue("confirmed", mailSubscriptionEntity.confirmed())
                .addValue("confirm_identifier", mailSubscriptionEntity.confirmIdentifier().toString())
                .addValue("email", mailSubscriptionEntity.email());

        var keyHolder = new GeneratedKeyHolder();

        template.update(sql, parameters, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<MailSubscriptionEntity> findSubscriptionsByServiceConfigurationId(long serviceConfigurationId) {
        var sql = "select s.* " +
                "    from mail_subscription s, " +
                "         mail_subscription_group g, " +
                "         service_configuration sc" +
                "   where g.mail_subscription_id = s.id " +
                "     and g.group_configuration_id = sc.group_configuration_id " +
                "     and sc.id = :service_configuration_id" +
                "     and s.confirmed = 1";

        return template.query(sql, Collections.singletonMap("service_configuration_id", serviceConfigurationId), DataClassRowMapper.newInstance(MailSubscriptionEntity.class));
    }

    @Override
    public boolean updateConfirmedByConfirmationUuid(UUID confirmationUuid) {
        var sql = "update mail_subscription set confirmed = 1 where confirm_identifier = :uuid";

        var updateCount = template.update(sql, Collections.singletonMap("uuid", confirmationUuid.toString()));

        return updateCount > 0;
    }
}
