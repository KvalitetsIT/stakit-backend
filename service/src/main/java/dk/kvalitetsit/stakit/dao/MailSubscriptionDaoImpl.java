package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MailSubscriptionDaoImpl implements MailSubscriptionDao {
    private static final Logger logger = LoggerFactory.getLogger(MailSubscriptionDaoImpl.class);

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
        logger.debug("Updating mail confirmation with uuid {}. Number of rows updated: {}.", confirmationUuid.toString(), updateCount);

        return updateCount > 0;
    }

    @Override
    public void deleteByEmail(String email) {
        var sql = "delete from mail_subscription where email = :email";

        template.update(sql, Collections.singletonMap("email", email));
    }

    @Override
    public int deleteByUuid(UUID uuid) {
        var sql = "delete from mail_subscription where uuid = :uuid";

        return template.update(sql, Collections.singletonMap("uuid", uuid.toString()));
    }


    @Override
    public List<MailSubscriptionEntity> findAnnouncementSubscriptions() {
        var sql = "select * from mail_subscription where confirmed = 1 and announcements = 1";

        return template.query(sql, DataClassRowMapper.newInstance(MailSubscriptionEntity.class));
    }
}
