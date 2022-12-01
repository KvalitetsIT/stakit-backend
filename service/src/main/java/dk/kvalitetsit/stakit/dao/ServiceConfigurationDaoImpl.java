package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ServiceConfigurationDaoImpl implements ServiceConfigurationDao {
    private static final Logger logger = LoggerFactory.getLogger(ServiceConfigurationDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public ServiceConfigurationDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(ServiceConfigurationEntity serviceConfigurationEntity) {
        logger.info("Inserting or updating entry in database.");

        var sql = "insert service_configuration(service, name, ignore_service_name, group_configuration_id) values(:service, :service_name, :ignore_service_name, :group_configuration_id)";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("service", serviceConfigurationEntity.service());
        parameterMap.put("service_name", serviceConfigurationEntity.name());
        parameterMap.put("ignore_service_name", serviceConfigurationEntity.ignoreServiceName());
        parameterMap.put("group_configuration_id", serviceConfigurationEntity.groupConfigurationId());

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
}
