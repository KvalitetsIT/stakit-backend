package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.entity.GroupedStatus;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusElementModel;
import dk.kvalitetsit.stakit.service.model.StatusGroupedModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class StatusGroupServiceImpl implements StatusGroupService {
    private final GroupedStatusDao groupedStatusDao;

    public StatusGroupServiceImpl(GroupedStatusDao groupedStatusDao) {
        this.groupedStatusDao = groupedStatusDao;
    }

    @Override
    @Transactional
    public List<StatusGroupedModel> getStatusGrouped() {
        var statusData = groupedStatusDao.getGroupedStatus();

        return mapStatus(statusData);
    }

    private List<StatusGroupedModel> mapStatus(List<GroupedStatus> statusData) {
        var groupMap = new HashMap<String, StatusGroupedModel>();
        statusData.forEach(x -> {
            var groupName =  x.groupName();
            var groupDescription = x.groupDescription();
            var statusGroupModel = groupMap.computeIfAbsent(groupName, k -> new StatusGroupedModel(groupName, new ArrayList<>(), groupDescription, x.groupUuid()));
            var status = x.status();
            if(x.serviceName() != null) {
                if(status == null) {
                    status = "OK";
                }
                statusGroupModel.status()
                        .add(new StatusElementModel(Status.valueOf(status), x.serviceName(), x.serviceDescription(), x.serviceUuid()));
            }
        });

        return new ArrayList<>(groupMap.values());
    }
}
