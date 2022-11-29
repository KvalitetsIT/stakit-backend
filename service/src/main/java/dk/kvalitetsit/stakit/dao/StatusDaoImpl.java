package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class StatusDaoImpl implements StatusDao {
    private static final Logger logger = LoggerFactory.getLogger(StatusDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public StatusDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void insertUpdate(StatusEntity statusEntity) {
        logger.info("Inserting or updating entry in database.");

        var sql = "insert status(status, status_time, message, status_configuration_id) values(:status, :statusTime, :message, :statusConfigurationId)";

        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("status", statusEntity.status());
        parameterMap.put("statusTime", Timestamp.from(statusEntity.statusTime().toInstant()));
        parameterMap.put("message", statusEntity.message());
        parameterMap.put("statusConfigurationId", statusEntity.statusConfigurationId());

        template.update(sql, parameterMap);
    }

    @Override
    public List<StatusEntity> findAll() {
        var sql = "select * from status";

        return template.query(sql, new DataClassRowMapper<>(StatusEntity.class));
    }
}
