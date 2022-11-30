package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;
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

public class StatusConfigurationDaoImpl implements StatusConfigurationDao {
    private static final Logger logger = LoggerFactory.getLogger(StatusConfigurationDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public StatusConfigurationDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(StatusConfigurationEntity statusConfigurationEntity) {
        logger.info("Inserting or updating entry in database.");

        var sql = "insert status_configuration(service, service_name, ignore_service_name, group_configuration_id) values(:service, :service_name, :ignore_service_name, :group_configuration_id)";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("service", statusConfigurationEntity.service());
        parameterMap.put("service_name", statusConfigurationEntity.serviceName());
        parameterMap.put("ignore_service_name", statusConfigurationEntity.ignoreServiceName());
        parameterMap.put("group_configuration_id", statusConfigurationEntity.groupConfigurationId());

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, new MapSqlParameterSource(parameterMap), keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<StatusConfigurationEntity> findAll() {
        var sql = "select * from status_configuration";

        return template.query(sql, new DataClassRowMapper<>(StatusConfigurationEntity.class));
    }

    @Override
    public StatusConfigurationEntity findByService(String service) {
        return template.queryForObject("select * from status_configuration where service = :service", Collections.singletonMap("service", service), new DataClassRowMapper<>(StatusConfigurationEntity.class));
    }
}
