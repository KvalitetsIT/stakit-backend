package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.service.mapper.ServiceMapper;
import dk.kvalitetsit.stakit.service.model.ServiceModel;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public List<ServiceModel> getServices() {
        var services = serviceConfigurationDao.findAllWithGroupId();

        return services.stream().map(ServiceMapper::mapEntityToService).toList();
    }

    @Override
    @Transactional
    public Optional<ServiceModel> getService(UUID uuid) {
        var service = serviceConfigurationDao.findByUuidWithGroupUuid(uuid);

        return service.map(ServiceMapper::mapEntityToService);
    }

    @Override
    @Transactional
    public boolean updateService(UUID uuid, ServiceModel serviceModel) {
        Optional<GroupConfigurationEntity> group = serviceModel.group() != null ? groupConfigurationDao.findByUuid(serviceModel.group()) : Optional.empty();

        if(serviceModel.group() != null && group.isEmpty()) {
            throw new IllegalArgumentException("Group not found");
        }

        return serviceConfigurationDao.updateByUuid(ServiceMapper.mapServiceToEntity(uuid, serviceModel, group));
    }

    @Override
    @Transactional
    public UUID createService(ServiceModel serviceModel) {
        var serviceUuid = UUID.randomUUID();

        Optional<GroupConfigurationEntity> group = serviceModel.group() != null ? groupConfigurationDao.findByUuid(serviceModel.group()) : Optional.empty();

        if(serviceModel.group() != null && group.isEmpty()) {
            return null;
        }

        serviceConfigurationDao.insert(ServiceMapper.mapServiceToEntity(serviceUuid, serviceModel, group));

        return serviceUuid;
    }

    @Override
    @Transactional
    public boolean deleteService(UUID uuid) {
        return serviceConfigurationDao.delete(uuid);
    }
}
