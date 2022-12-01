package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class ServiceStatusDaoImpl implements ServiceStatusDao {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStatusDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public ServiceStatusDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void insertUpdate(ServiceStatusEntity serviceStatusEntity) {
        logger.info("Inserting or updating entry in database.");

        var sql = "insert service_status(status, status_time, message, service_configuration_id) values(:status, :statusTime, :message, :serviceConfigurationId)";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("status", serviceStatusEntity.status());
        parameterMap.put("statusTime", Timestamp.from(serviceStatusEntity.statusTime().toInstant()));
        parameterMap.put("message", serviceStatusEntity.message());
        parameterMap.put("serviceConfigurationId", serviceStatusEntity.serviceConfigurationId());

        template.update(sql, parameterMap);
    }

    @Override
    public List<ServiceStatusEntity> findAll() {
        var sql = "select * from service_status";

        return template.query(sql, new DataClassRowMapper<>(ServiceStatusEntity.class));
    }
}
