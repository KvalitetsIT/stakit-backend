package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.HashMap;
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

        var sql = "insert group_configuration(uuid, group_name, display_order) values(:uuid, :group_name, :display_order)";

        var parameters = new MapSqlParameterSource()
                .addValue("uuid", groupConfigurationEntity.uuid().toString())
                .addValue("group_name", groupConfigurationEntity.groupName())
                .addValue("display_order", groupConfigurationEntity.displayOrder());

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, parameters, keyHolder);

        return keyHolder.getKey().longValue();

    }

    @Override
    public List<GroupConfigurationEntity> findAll() {
        var sql = "select * from group_configuration";

        return template.query(sql, new DataClassRowMapper<>(GroupConfigurationEntity.class));
    }

    @Override
    public boolean update(GroupConfigurationEntity groupConfigurationEntity) {
        var sql = "update group_configuration set group_name = :group_name, display_order = :display_order where uuid = :uuid";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("group_name", groupConfigurationEntity.groupName());
        parameterMap.put("display_order", groupConfigurationEntity.displayOrder());
        parameterMap.put("uuid", groupConfigurationEntity.uuid().toString());


        return template.update(sql, parameterMap) != 0;
    }
}
