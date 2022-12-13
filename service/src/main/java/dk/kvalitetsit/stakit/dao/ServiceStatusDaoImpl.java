package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ServiceStatusDaoImpl implements ServiceStatusDao {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStatusDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public ServiceStatusDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long insert(ServiceStatusEntity serviceStatusEntity) {
        logger.info("Inserting or updating entry in database.");

        var sql = "insert service_status(status, status_time, message, service_configuration_id) values(:status, :statusTime, :message, :serviceConfigurationId)";

        var parameterMap = new MapSqlParameterSource()
                .addValue("status", serviceStatusEntity.status())
                .addValue("statusTime", Timestamp.from(serviceStatusEntity.statusTime().toInstant()))
                .addValue("message", serviceStatusEntity.message())
                .addValue("serviceConfigurationId", serviceStatusEntity.serviceConfigurationId());

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, parameterMap, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public List<ServiceStatusEntity> findAll() {
        var sql = "select * from service_status";

        return template.query(sql, new DataClassRowMapper<>(ServiceStatusEntity.class));
    }

    @Override
    public Optional<ServiceStatusEntity> findLatest(String service) {
        var sql = "select s.* from service_status s, service_configuration sc where s.service_configuration_id = sc.id order by status_time desc limit 1";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("service", service), DataClassRowMapper.newInstance(ServiceStatusEntity.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("No service status found for service {}", service);

            return Optional.empty();
        }
    }

    @Override
    public Optional<ServiceStatusEntity> findById(long id) {
        var sql = "select * from service_status where id = :id";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("id", id), DataClassRowMapper.newInstance(ServiceStatusEntity.class)));
        }
        catch(EmptyResultDataAccessException e) {
            logger.debug("Service status with id {} not found.", id);

            return Optional.empty();
        }
    }
}
