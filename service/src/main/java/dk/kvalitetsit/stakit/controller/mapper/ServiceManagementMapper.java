package dk.kvalitetsit.stakit.controller.mapper;

import org.openapitools.model.Service;
import org.openapitools.model.ServiceCreate;
import org.openapitools.model.ServiceUpdate;
import org.openapitools.model.Services;

import java.util.List;

public class ServiceManagementMapper {
    public static List<Services> mapServices(List<dk.kvalitetsit.stakit.service.model.Service> services) {
        return services.stream().map(ServiceManagementMapper::mapServices).toList();
    }

    private static Services mapServices(dk.kvalitetsit.stakit.service.model.Service service) {
        var s = new Services();
        s.setServiceIdentifier(service.serviceIdentifier());
        s.setName(service.name());
        s.setIgnoreServiceName(service.ignoreServiceName());
        s.setGroup(service.group());
        s.setUuid(service.uuid());

        return s;
    }

    public static Service mapService(dk.kvalitetsit.stakit.service.model.Service service) {
        var s = new Service();
        s.setServiceIdentifier(service.serviceIdentifier());
        s.setName(service.name());
        s.setIgnoreServiceName(service.ignoreServiceName());
        s.setGroup(service.group());

        return s;
    }

    public static dk.kvalitetsit.stakit.service.model.Service mapUpdate(ServiceUpdate serviceUpdate) {
        return new dk.kvalitetsit.stakit.service.model.Service(serviceUpdate.getName(), serviceUpdate.getServiceIdentifier(), serviceUpdate.getIgnoreServiceName(), serviceUpdate.getGroup(), null);
    }

    public static dk.kvalitetsit.stakit.service.model.Service mapCreate(ServiceCreate serviceCreate) {
        return new dk.kvalitetsit.stakit.service.model.Service(serviceCreate.getName(), serviceCreate.getServiceIdentifier(), serviceCreate.getIgnoreServiceName(), serviceCreate.getGroup(), null);
    }
}
