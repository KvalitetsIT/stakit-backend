package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.StatusGroupedModel;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;
import org.openapitools.model.StatusGroup;
import org.openapitools.model.Subscribe;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StakitMapper {

    public static SubscriptionModel mapSubscription(Subscribe subscribe) {
        return new SubscriptionModel(subscribe.getEmail(), subscribe.getGroups(), subscribe.getAnnouncements());
    }

    public static List<StatusGroup> mappedStatusGroups(List<StatusGroupedModel> groupStatus){
        return groupStatus.stream()
                .map(StakitMapper::mapGroup)
                .collect(Collectors.toList());
    }
    private static StatusGroup mapGroup(StatusGroupedModel statusGroupedModel) {
        var statusGroup = new StatusGroup();
        statusGroup.setName(statusGroupedModel.groupName());
        statusGroup.setServices(new ArrayList<>());
        statusGroup.setDescription(statusGroupedModel.description());
        statusGroup.setUuid(statusGroupedModel.groupUuid());
        statusGroup.setDisplay(statusGroupedModel.display());
        statusGroupedModel.status().forEach(x -> {
            var s = new org.openapitools.model.ServiceStatus();
            s.setName(x.statusName());
            s.setStatus(org.openapitools.model.ServiceStatus.StatusEnum.fromValue(x.status().toString()));
            s.setDescription(x.description());
            s.setUuid(x.uuid());

            statusGroup.addServicesItem(s);
        });

        return statusGroup;
    }
}
