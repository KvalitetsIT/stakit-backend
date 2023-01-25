package dk.kvalitetsit.stakit.service.mapper;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import dk.kvalitetsit.stakit.service.model.ServiceModel;

import java.util.UUID;

public class ServiceMapper {
    public static ServiceModel mapEntityToService(ServiceConfigurationEntityWithGroupUuid input) {
        return new ServiceModel(input.name(), input.service(), input.ignoreServiceName(), input.groupUuid(), input.uuid(), input.status(), input.description());
    }
    public static ServiceConfigurationEntity mapServiceToEntity(UUID uuid, ServiceModel serviceModel, long group) {
        return ServiceConfigurationEntity.createInstance(serviceModel.serviceIdentifier(), uuid, serviceModel.name(), serviceModel.ignoreServiceName(), group, serviceModel.status(), serviceModel.description());
    }
}
