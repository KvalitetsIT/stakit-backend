package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.ServiceModel;
import org.openapitools.model.Service;
import org.openapitools.model.ServiceCreate;
import org.openapitools.model.ServiceUpdate;
import org.openapitools.model.Services;

import java.util.List;

public class ServiceManagementMapper {
    public static List<Services> mapServices(List<ServiceModel> serviceModels) {
        return serviceModels.stream().map(ServiceManagementMapper::mapServices).toList();
    }

    private static Services mapServices(ServiceModel serviceModel) {
        var s = new Services();
        s.setServiceIdentifier(serviceModel.serviceIdentifier());
        s.setName(serviceModel.name());
        s.setIgnoreServiceName(serviceModel.ignoreServiceName());
        s.setGroup(serviceModel.group());
        s.setUuid(serviceModel.uuid());
        s.setDescription(serviceModel.description());

        return s;
    }

    public static Service mapService(ServiceModel serviceModel) {
        var s = new Service();
        s.setServiceIdentifier(serviceModel.serviceIdentifier());
        s.setName(serviceModel.name());
        s.setIgnoreServiceName(serviceModel.ignoreServiceName());
        s.setGroup(serviceModel.group());
        s.setDescription(serviceModel.description());

        return s;
    }

    public static ServiceModel mapUpdate(ServiceUpdate serviceUpdate) {
        return new ServiceModel(serviceUpdate.getName(), serviceUpdate.getServiceIdentifier(), serviceUpdate.getIgnoreServiceName(), serviceUpdate.getGroup(), null, serviceUpdate.getDescription());
    }

    public static ServiceModel mapCreate(ServiceCreate serviceCreate) {
        return new ServiceModel(serviceCreate.getName(), serviceCreate.getServiceIdentifier(), serviceCreate.getIgnoreServiceName(), serviceCreate.getGroup(), null, serviceCreate.getDescription());
    }
}
