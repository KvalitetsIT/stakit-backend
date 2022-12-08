package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.AnnouncementEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnnouncementDaoImpl implements AnnouncementDao {
    private final static Logger logger = LoggerFactory.getLogger(AnnouncementDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public AnnouncementDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(AnnouncementEntity announcementEntity) {
        var sql = "insert into announcement(uuid, from_datetime, to_datetime, subject, message) values(:uuid, :fromDatetime, :toDatetime, :subject, :message)";

        var parameters = new MapSqlParameterSource()
                .addValue("uuid", announcementEntity.uuid().toString())
                .addValue("fromDatetime", announcementEntity.fromDatetime())
                .addValue("toDatetime", announcementEntity.toDatetime())
                .addValue("subject", announcementEntity.subject())
                .addValue("message", announcementEntity.message())
                ;

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, parameters, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<AnnouncementEntity> getByUuid(UUID uuid) {
        var sql = "select * from announcement where uuid = :uuid";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("uuid", uuid.toString()), DataClassRowMapper.newInstance(AnnouncementEntity.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Announcement with uuid {} not found.", uuid);

            return Optional.empty();
        }
    }

    @Override
    public boolean updateByUuid(AnnouncementEntity announcementEntity) {
        var sql = "update announcement " +
                "     set from_datetime = :fromDatetime, " +
                "         to_datetime = :toDatetime, " +
                "         subject = :subject, " +
                "         message = :message " +
                "    where uuid =:uuid";

        var parameters = new MapSqlParameterSource()
                .addValue("uuid", announcementEntity.uuid().toString())
                .addValue("fromDatetime", announcementEntity.fromDatetime())
                .addValue("toDatetime", announcementEntity.toDatetime())
                .addValue("subject", announcementEntity.subject())
                .addValue("message", announcementEntity.message())
                ;

        return template.update(sql, parameters) > 0;
    }

    @Override
    public boolean deleteByUuid(UUID uuid) {
        var sql = "delete from announcement where uuid = :uuid";

        return template.update(sql, Collections.singletonMap("uuid", uuid.toString())) > 0;
    }

    @Override
    public List<AnnouncementEntity> getAnnouncements(OffsetDateTime toDatetime) {
        var sql = "select * from announcement where to_datetime > :to_datetime order by to_datetime desc";

        return template.query(sql, Collections.singletonMap("to_datetime", toDatetime), DataClassRowMapper.newInstance(AnnouncementEntity.class));
    }
}
