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
        var sql = "select service_name, " +
                "         group_name, " +
                "         status " +
                "   from status s, " +
                "        status_configuration sc " +
                "   left outer join group_configuration gc " +
                "        on gc.id = sc.group_configuration_id  " +
                "  where s.status_configuration_id = sc.id " +
                "    and s.id = (select max(id) from status where sc.id = status.status_configuration_id)" +
                " order by group_name, service_name";

        return template.query(sql, DataClassRowMapper.newInstance(GroupedStatus.class));
    }
}
