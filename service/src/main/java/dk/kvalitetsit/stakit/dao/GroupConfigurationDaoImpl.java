package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.*;

public class GroupConfigurationDaoImpl implements GroupConfigurationDao {
    private static final Logger logger = LoggerFactory.getLogger(GroupConfigurationDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public GroupConfigurationDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(GroupConfigurationEntity groupConfigurationEntity) {
        logger.debug("Inserting new group.");

        var sql = "insert group_configuration(uuid, name, display_order, description, display, expanded) values(:uuid, :group_name, :display_order, :description, :display, :expanded)";

        var parameters = new MapSqlParameterSource()
                .addValue("uuid", groupConfigurationEntity.uuid().toString())
                .addValue("group_name", groupConfigurationEntity.name())
                .addValue("display_order", groupConfigurationEntity.displayOrder())
                .addValue("description", groupConfigurationEntity.description())
                .addValue("display", groupConfigurationEntity.display())
                .addValue("expanded", groupConfigurationEntity.expanded());


        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, parameters, keyHolder);

        return keyHolder.getKey().longValue();

    }

    @Override
    public List<GroupConfigurationEntity> findAll() {
        var sql = "select * from group_configuration order by display_order";

        return template.query(sql, new DataClassRowMapper<>(GroupConfigurationEntity.class));
    }

    @Override
    public boolean update(GroupConfigurationEntity groupConfigurationEntity) {
        var sql = "update group_configuration set name = :group_name, display_order = :display_order, description = :description, display = :display, expanded = :expanded where uuid = :uuid";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("group_name", groupConfigurationEntity.name());
        parameterMap.put("display_order", groupConfigurationEntity.displayOrder());
        parameterMap.put("uuid", groupConfigurationEntity.uuid().toString());
        parameterMap.put("description", groupConfigurationEntity.description());
        parameterMap.put("display", groupConfigurationEntity.display());
        parameterMap.put("expanded", groupConfigurationEntity.expanded());


        return template.update(sql, parameterMap) != 0;
    }

    @Override
    public Optional<GroupConfigurationEntity> findByUuid(UUID group) {
        var sql = "select * from group_configuration where uuid = :uuid";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("uuid", group.toString()), DataClassRowMapper.newInstance(GroupConfigurationEntity.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Group with uuid {} not found.", group);
            return Optional.empty();
        }
    }

    @Override
    public Optional<GroupConfigurationEntity> findById(long id) {
        var sql = "select * from group_configuration where id = :id";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("id", id), DataClassRowMapper.newInstance(GroupConfigurationEntity.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Group configuration with id {} not found.", id);

            return Optional.empty();
        }
    }

    @Override
    public boolean delete(UUID uuid) {
        var sql = "delete from group_configuration where uuid = :uuid";

        return template.update(sql, Collections.singletonMap("uuid", uuid.toString())) > 0;
    }

    @Override
    public Optional<Long> findDefaultGroupId() {
        var sql = "select min(id) as id from group_configuration where name = 'Default'";

        return Optional.ofNullable(template.queryForObject(sql, Collections.emptyMap(), Long.class));
    }

    @Override
    public Long createDefaultGroup() {
        GroupConfigurationEntity groupConfigurationEntity = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "Default", 0, "Default group", true, true);

        return insert(groupConfigurationEntity);
    }
}
