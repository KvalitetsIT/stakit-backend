package dk.kvalitetsit.stakit.service.mapper;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import dk.kvalitetsit.stakit.service.model.Service;

import java.util.Optional;
import java.util.UUID;

public class ServiceMapper {
    public static Service mapEntityToService(ServiceConfigurationEntityWithGroupUuid input) {
        return new Service(input.name(), input.service(), input.ignoreServiceName(), input.groupUuid(), input.uuid());
    }

    public static ServiceConfigurationEntity mapServiceToEntity(UUID uuid, Service service, Optional<GroupConfigurationEntity> group) {
        return ServiceConfigurationEntity.createInstance(service.serviceIdentifier(), uuid, service.name(), service.ignoreServiceName(), group.map(GroupConfigurationEntity::id).orElse(null));
    }
}
