package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.List;

public class GroupConfigurationDaoImpl implements GroupConfigurationDao {
    private static final Logger logger = LoggerFactory.getLogger(GroupConfigurationDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public GroupConfigurationDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(GroupConfigurationEntity groupConfigurationEntity) {
        logger.debug("Inserting new group.");

        var sql = "insert group_configuration(group_name) values(:group_name)";

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, new MapSqlParameterSource("group_name", groupConfigurationEntity.groupName()), keyHolder);

        return keyHolder.getKey().longValue();

    }

    @Override
    public List<GroupConfigurationEntity> findAll() {
        var sql = "select * from group_configuration";

        return template.query(sql, new DataClassRowMapper<>(GroupConfigurationEntity.class));
    }
}
