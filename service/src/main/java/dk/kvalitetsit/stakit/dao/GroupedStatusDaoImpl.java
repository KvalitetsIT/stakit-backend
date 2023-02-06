package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupedStatus;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class GroupedStatusDaoImpl implements GroupedStatusDao {
    private final NamedParameterJdbcTemplate template;

    public GroupedStatusDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<GroupedStatus> getGroupedStatus() {
        var sql = "select sc.name as service_name, " +
                "         gc.name as group_name, " +
                "         sc.description as service_description, " +
                "         gc.description as group_description, " +
                "         gc.uuid as group_uuid, " +
                "         status " +
                "   from group_configuration gc " +
                "   left outer join service_configuration sc" +
                "        on gc.id = sc.group_configuration_id" +
                "   left outer join service_status s " +
                "        on s.service_configuration_id = sc.id and " +
                "           s.id = (select max(id) from service_status where sc.id = service_status.service_configuration_id) " +
                " order by group_name, service_name";

        return template.query(sql, DataClassRowMapper.newInstance(GroupedStatus.class));
    }
}
