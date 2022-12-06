package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.service.mapper.ServiceMapper;
import dk.kvalitetsit.stakit.service.model.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceConfigurationDao serviceConfigurationDao;
    private final GroupConfigurationDao groupConfigurationDao;

    public ServiceManagementServiceImpl(ServiceConfigurationDao serviceConfigurationDao, GroupConfigurationDao groupConfigurationDao) {
        this.serviceConfigurationDao = serviceConfigurationDao;
        this.groupConfigurationDao = groupConfigurationDao;
    }

    @Override
    public List<Service> getServices() {
        var services = serviceConfigurationDao.findAllWithGroupId();

        return services.stream().map(ServiceMapper::mapEntityToService).toList();
    }

    @Override
    public Service getService(UUID uuid) {
        var service = serviceConfigurationDao.findByUuidWithGroupUuid(uuid);

        return service.map(ServiceMapper::mapEntityToService).orElse(null);
    }

    @Override
    public boolean updateService(UUID uuid, Service service) {
        Optional<GroupConfigurationEntity> group = service.group() != null ? groupConfigurationDao.findByUuid(service.group()) : Optional.empty();

        if(service.group() != null && group.isEmpty()) {
            throw new IllegalArgumentException("Group not found");
        }

        return serviceConfigurationDao.updateByUuid(ServiceMapper.mapServiceToEntity(uuid, service, group));
    }

    @Override
    public UUID createService(Service service) {
        var serviceUuid = UUID.randomUUID();

        Optional<GroupConfigurationEntity> group = service.group() != null ? groupConfigurationDao.findByUuid(service.group()) : Optional.empty();

        if(service.group() != null && group.isEmpty()) {
            return null;
        }

        serviceConfigurationDao.insert(ServiceMapper.mapServiceToEntity(serviceUuid, service, group));

        return serviceUuid;
    }
}
