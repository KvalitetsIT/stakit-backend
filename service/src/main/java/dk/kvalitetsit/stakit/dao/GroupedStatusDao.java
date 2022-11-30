package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupedStatus;

import java.util.List;

public interface GroupedStatusDao {
    List<GroupedStatus> getGroupedStatus();
}
