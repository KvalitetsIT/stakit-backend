package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;

public class MailSubscriptionDaoImpl implements MailSubscriptionDao {
    private final NamedParameterJdbcTemplate template;
    public MailSubscriptionDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(MailSubscriptionEntity mailSubscriptionEntity) {
        var sql = "insert into mail_subscription(uuid, announcements, confirmed, confirm_identifier, email) values(:uuid, :announcements, :confirmed, :confirm_identifier, email)";

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
}
