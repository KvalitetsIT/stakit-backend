package dk.kvalitetsit.stakit.controller.mapper;

import dk.kvalitetsit.stakit.service.model.ServiceModel;
import org.openapitools.model.Service;
import org.openapitools.model.ServiceCreate;
import org.openapitools.model.ServiceUpdate;

import java.util.List;

public class ServiceManagementMapper {
    public static List<Service> mapServices(List<ServiceModel> serviceModels) {
        return serviceModels.stream().map(ServiceManagementMapper::mapService).toList();
    }

    public static Service mapService(ServiceModel serviceModel) {
        var s = new Service();
        s.setServiceIdentifier(serviceModel.serviceIdentifier());
        s.setName(serviceModel.name());
        s.setIgnoreServiceName(serviceModel.ignoreServiceName());
        s.setGroup(serviceModel.group());
        s.setUuid(serviceModel.uuid());
        if (serviceModel.status() != null) {
            s.setStatus(Service.StatusEnum.fromValue(serviceModel.status()));
        }
        s.setDescription(serviceModel.description());

        return s;
    }

    public static ServiceModel mapUpdate(ServiceUpdate serviceUpdate) {
        return new ServiceModel(serviceUpdate.getName(), serviceUpdate.getServiceIdentifier(), serviceUpdate.getIgnoreServiceName(), serviceUpdate.getGroup(), null, null, serviceUpdate.getDescription());
    }

    public static ServiceModel mapCreate(ServiceCreate serviceCreate) {
        return new ServiceModel(serviceCreate.getName(), serviceCreate.getServiceIdentifier(), serviceCreate.getIgnoreServiceName(), serviceCreate.getGroup(), null, null, serviceCreate.getDescription());
    }
}
