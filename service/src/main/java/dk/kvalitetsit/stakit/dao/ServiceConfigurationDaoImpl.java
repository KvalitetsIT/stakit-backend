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

        var sql = "insert service_configuration(service, uuid, name, ignore_service_name, group_configuration_id, description) values(:service, :uuid, :service_name, :ignore_service_name, :group_configuration_id, :description)";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("service", serviceConfigurationEntity.service());
        parameterMap.put("service_name", serviceConfigurationEntity.name());
        parameterMap.put("ignore_service_name", serviceConfigurationEntity.ignoreServiceName());
        parameterMap.put("group_configuration_id", serviceConfigurationEntity.groupConfigurationId());
        parameterMap.put("uuid", serviceConfigurationEntity.uuid().toString());
        parameterMap.put("description", serviceConfigurationEntity.description());

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, new MapSqlParameterSource(parameterMap), keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<ServiceConfigurationEntity> findAll() {
        var sql = "select sc.name as name, " +
                "         sc.id as id, " +
                "         sc.uuid as uuid, " +
                "         sc.service as service, " +
                "         sc.ignore_service_name, " +
                "         sc.description as description, " +
                "         sc.group_configuration_id as group_configuration_id, " +
                "         status " +
                "   from service_configuration sc " +
                "   left outer join service_status s " +
                "        on s.service_configuration_id = sc.id and " +
                "           s.id = (select max(id) from service_status where sc.id = service_status.service_configuration_id)";

        return template.query(sql, new DataClassRowMapper<>(ServiceConfigurationEntity.class));
    }

    @Override
    public ServiceConfigurationEntity findByService(String service) {
        var sql = "select sc.name as name, " +
                "         sc.id as id, " +
                "         sc.uuid as uuid, " +
                "         sc.service as service, " +
                "         sc.ignore_service_name, " +
                "         sc.description as description, " +
                "         sc.group_configuration_id as group_configuration_id, " +
                "         status " +
                "   from service_configuration sc " +
                "   left outer join service_status s " +
                "        on s.service_configuration_id = sc.id and " +
                "           s.id = (select max(id) from service_status where sc.id = service_status.service_configuration_id) " +
                "   where sc.service = :service";

        return template.queryForObject(sql, Collections.singletonMap("service", service), new DataClassRowMapper<>(ServiceConfigurationEntity.class));
    }

    @Override
    public Optional<ServiceConfigurationEntityWithGroupUuid> findByUuidWithGroupUuid(UUID serviceUuid) {
        var sql = "select sc.id, " +
                "         sc.uuid, " +
                "         sc.service, " +
                "         sc.name, " +
                "         sc.ignore_service_name, " +
                "         ss.status, " +
                "         sc.description, " +
                "         gc.uuid as group_uuid " +
                "   from service_configuration sc " +
                "   left outer join group_configuration gc " +
                "        on gc.id = sc.group_configuration_id " +
                "   left outer join service_status ss " +
                "        on ss.service_configuration_id = sc.id and " +
                "           ss.id = (select max(id) from service_status where sc.id = service_status.service_configuration_id) " +
                "   where sc.uuid = :uuid " +
                "  order by gc.display_order";

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
                "         ignore_service_name = :ignore_service_name, " +
                "         description = :description " +
                "   where uuid = :uuid";

        var parameters = new MapSqlParameterSource()
                .addValue("service", serviceConfigurationEntity.service())
                .addValue("name", serviceConfigurationEntity.name())
                .addValue("group_configuration_id", serviceConfigurationEntity.groupConfigurationId())
                .addValue("uuid", serviceConfigurationEntity.uuid().toString())
                .addValue("ignore_service_name", serviceConfigurationEntity.ignoreServiceName())
                .addValue("description", serviceConfigurationEntity.description());

        var updateCount = template.update(sql, parameters);

        return updateCount > 0;
    }

    @Override

    public List<ServiceConfigurationEntityWithGroupUuid> findAllWithGroupId() {
        var sql = "select sc.id, " +
                "         sc.uuid, " +
                "         sc.service, " +
                "         sc.name, " +
                "         sc.ignore_service_name, " +
                "         ss.status, " +
                "         sc.description, " +
                "         gc.uuid as group_uuid " +
                "    from service_configuration sc " +
                "    left outer join group_configuration gc " +
                "         on gc.id = sc.group_configuration_id " +
                "   left outer join service_status ss " +
                "        on ss.service_configuration_id = sc.id and " +
                "           ss.id = (select max(id) from service_status where sc.id = service_status.service_configuration_id) " +
                "   order by gc.display_order";

        return template.query(sql, DataClassRowMapper.newInstance(ServiceConfigurationEntityWithGroupUuid.class));
    }

    @Override
    public Optional<ServiceConfigurationEntity> findById(long id) {

        var sql = "select sc.name as name, " +
                "         sc.id as id, " +
                "         sc.uuid as uuid, " +
                "         sc.service as service, " +
                "         sc.ignore_service_name, " +
                "         sc.description as description, " +
                "         sc.group_configuration_id as group_configuration_id, " +
                "         status " +
                "   from service_configuration sc " +
                "   left outer join service_status s " +
                "        on s.service_configuration_id = sc.id and " +
                "           s.id = (select max(id) from service_status where sc.id = service_status.service_configuration_id) " +
                "   where sc.id = :id";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("id", id), DataClassRowMapper.newInstance(ServiceConfigurationEntity.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Service configuration not found for id {}", id);

            return Optional.empty();
        }
    }

    @Override
    public boolean delete(UUID uuid) {
        var sql = "delete from service_configuration where uuid = :uuid";

        return template.update(sql, Collections.singletonMap("uuid", uuid.toString())) > 0;
    }

    @Override
    public List<ServiceConfigurationEntity> findByGroupUuid(UUID uuid) {
        var sql = "select sc.name as name, " +
                "         sc.id as id, " +
                "         sc.uuid as uuid, " +
                "         sc.service as service, " +
                "         sc.ignore_service_name, " +
                "         sc.description as description, " +
                "         sc.group_configuration_id as group_configuration_id, " +
                "         status " +
                "   from service_configuration sc " +
                "   left outer join group_configuration gc" +
                "        on sc.group_configuration_id = gc.id" +
                "   left outer join service_status s " +
                "        on s.service_configuration_id = sc.id and " +
                "           s.id = (select max(id) from service_status where sc.id = service_status.service_configuration_id) " +
                "   where gc.id = sc.group_configuration_id and gc.uuid = :uuid";

        return template.query(sql, Collections.singletonMap("uuid", uuid.toString()), DataClassRowMapper.newInstance(ServiceConfigurationEntity.class));
    }
}
