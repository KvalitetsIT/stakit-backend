package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.entity.GroupedStatus;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusElement;
import dk.kvalitetsit.stakit.service.model.StatusGrouped;

import java.util.*;

public class StatusGroupServiceImpl implements StatusGroupService {
    private final GroupedStatusDao groupedStatusDao;

    public StatusGroupServiceImpl(GroupedStatusDao groupedStatusDao) {
        this.groupedStatusDao = groupedStatusDao;
    }

    @Override
    public List<StatusGrouped> getStatusGrouped() {
        var statusData = groupedStatusDao.getGroupedStatus();
        if(statusData.isEmpty()) {
            return Collections.singletonList(new StatusGrouped("Default", Collections.emptyList()));
        }

        return mapStatus(statusData);
    }

    private List<StatusGrouped> mapStatus(List<GroupedStatus> statusData) {
        var groupMap = new HashMap<String, StatusGrouped>();
        statusData.forEach(x -> {
            var groupName = x.groupName() == null ? "Default" : x.groupName();
            groupMap.computeIfAbsent(groupName, k -> new StatusGrouped(groupName, new ArrayList<>())).status()
                    .add(new StatusElement(Status.valueOf(x.status()), x.serviceName()));
        });

        return new ArrayList<>(groupMap.values());
    }
}
