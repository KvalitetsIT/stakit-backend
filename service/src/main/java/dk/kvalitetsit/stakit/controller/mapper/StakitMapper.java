package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.StatusGroupedModel;
import dk.kvalitetsit.stakit.service.model.StatusPeriodModel;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;
import org.openapitools.model.StatusGroup;
import org.openapitools.model.StatusPeriod;
import org.openapitools.model.Subscribe;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StakitMapper {

    public static SubscriptionModel mapSubscription(Subscribe subscribe) {
        return new SubscriptionModel(null, subscribe.getEmail(), subscribe.getGroups(), subscribe.getAnnouncements());
    }

    public static List<StatusGroup> mappedStatusGroups(List<StatusGroupedModel> groupStatus){
        return groupStatus.stream()
                .map(StakitMapper::mapGroup)
                .collect(Collectors.toList());
    }
    public static List<StatusPeriod> mapStatusPeriods(List<StatusPeriodModel> periods) {
        return periods.stream()
                .map(StakitMapper::mapStatusPeriod)
                .collect(Collectors.toList());
    }

    private static StatusPeriod mapStatusPeriod(StatusPeriodModel period) {
        var statusPeriod = new StatusPeriod();
        statusPeriod.setStatus(StatusPeriod.StatusEnum.fromValue(period.status().toString()));
        statusPeriod.setFrom(period.from());
        statusPeriod.setTo(period.to());
        statusPeriod.setMessage(period.message());

        return statusPeriod;
    }

    private static StatusGroup mapGroup(StatusGroupedModel statusGroupedModel) {
        var statusGroup = new StatusGroup();
        statusGroup.setName(statusGroupedModel.groupName());
        statusGroup.setServices(new ArrayList<>());
        statusGroup.setDescription(statusGroupedModel.description());
        statusGroup.setUuid(statusGroupedModel.groupUuid());
        statusGroup.setDisplay(statusGroupedModel.display());
        statusGroup.setExpanded(statusGroupedModel.expanded());

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
