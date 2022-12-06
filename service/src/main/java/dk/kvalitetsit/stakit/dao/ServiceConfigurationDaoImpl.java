package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.*;

public class ServiceConfigurationDaoImpl implements ServiceConfigurationDao {
    private static final Logger logger = LoggerFactory.getLogger(ServiceConfigurationDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public ServiceConfigurationDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(ServiceConfigurationEntity serviceConfigurationEntity) {
        logger.info("Inserting or updating entry in database.");

        var sql = "insert service_configuration(service, uuid, name, ignore_service_name, group_configuration_id) values(:service, :uuid, :service_name, :ignore_service_name, :group_configuration_id)";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("service", serviceConfigurationEntity.service());
        parameterMap.put("service_name", serviceConfigurationEntity.name());
        parameterMap.put("ignore_service_name", serviceConfigurationEntity.ignoreServiceName());
        parameterMap.put("group_configuration_id", serviceConfigurationEntity.groupConfigurationId());
        parameterMap.put("uuid", serviceConfigurationEntity.uuid().toString());

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, new MapSqlParameterSource(parameterMap), keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<ServiceConfigurationEntity> findAll() {
        var sql = "select * from service_configuration";

        return template.query(sql, new DataClassRowMapper<>(ServiceConfigurationEntity.class));
    }

    @Override
    public ServiceConfigurationEntity findByService(String service) {
        return template.queryForObject("select * from service_configuration where service = :service", Collections.singletonMap("service", service), new DataClassRowMapper<>(ServiceConfigurationEntity.class));
    }

    @Override
    public Optional<ServiceConfigurationEntityWithGroupUuid> findByUuidWithGroupUuid(UUID serviceUuid) {
        var sql = "select s.id, " +
                "         s.uuid, " +
                "         s.service, " +
                "         s.name, " +
                "         s.ignore_service_name, " +
                "         g.uuid as group_uuid " +
                "   from service_configuration s " +
                "   left outer join group_configuration g " +
                "        on g.id = s.group_configuration_id " +
                "   where s.uuid = :uuid " +
                "  order by g.display_order";

        try {
            var dbResult = template.queryForObject(sql, Collections.singletonMap("uuid", serviceUuid.toString()), DataClassRowMapper.newInstance(ServiceConfigurationEntityWithGroupUuid.class));
            return Optional.ofNullable(dbResult);
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Service Configuration with uuid {} not found.", serviceUuid);
            return Optional.empty();
        }
    }

    @Override
    public boolean updateByUuid(ServiceConfigurationEntity serviceConfigurationEntity) {
        var sql = "update service_configuration " +
                "     set service = :service, " +
                "         name = :name, " +
                "         group_configuration_id = :group_configuration_id," +
                "         ignore_service_name = :ignore_service_name " +
                "   where uuid = :uuid";

        var parameters = new MapSqlParameterSource()
                .addValue("service", serviceConfigurationEntity.service())
                .addValue("name", serviceConfigurationEntity.name())
                .addValue("group_configuration_id", serviceConfigurationEntity.groupConfigurationId())
                .addValue("uuid", serviceConfigurationEntity.uuid().toString())
                .addValue("ignore_service_name", serviceConfigurationEntity.ignoreServiceName());

        var updateCount = template.update(sql, parameters);

        return updateCount > 0;
    }

    @Override

    public List<ServiceConfigurationEntityWithGroupUuid> findAllWithGroupId() {
        var sql = "select s.id, " +
                "         s.uuid, " +
                "         s.service, " +
                "         s.name, " +
                "         s.ignore_service_name, " +
                "         g.uuid as group_uuid " +
                "    from service_configuration s " +
                "    left outer join group_configuration g " +
                "         on g.id = s.group_configuration_id " +
                "   order by g.display_order";

        return template.query(sql, DataClassRowMapper.newInstance(ServiceConfigurationEntityWithGroupUuid.class));
    }
}
