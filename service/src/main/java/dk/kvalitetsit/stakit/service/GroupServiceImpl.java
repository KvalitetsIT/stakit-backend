package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.GroupGetModel;
import dk.kvalitetsit.stakit.service.model.GroupModel;
import dk.kvalitetsit.stakit.service.model.ServiceModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupServiceImpl implements GroupService {
    private final GroupConfigurationDao groupConfigurationDao;
    private final ServiceConfigurationDao serviceConfigurationDao;

    public GroupServiceImpl(GroupConfigurationDao groupConfigurationDao, ServiceConfigurationDao serviceConfigurationDao) {
        this.groupConfigurationDao = groupConfigurationDao;
        this.serviceConfigurationDao = serviceConfigurationDao;
    }

    @Override
    @Transactional
    public UUID createGroup(GroupModel groupModel) {
        var uuid = UUID.randomUUID();

        groupConfigurationDao.insert(GroupConfigurationEntity.createInstance(uuid, groupModel.name(), groupModel.displayOrder(), groupModel.description()));

        return uuid;
    }

    @Override
    @Transactional
    public boolean updateGroup(GroupModel groupModel) {
        return groupConfigurationDao.update(GroupConfigurationEntity.createInstance(groupModel.uuid(), groupModel.name(), groupModel.displayOrder(), groupModel.description()));
    }

    @Override
    @Transactional
    public List<GroupGetModel> getGroups() {
        var dbResult = groupConfigurationDao.findAll();

        return dbResult.stream().map(x -> new GroupGetModel(x.uuid(), x.name(), x.displayOrder(), getServiceUuidList(serviceConfigurationDao.findByGroupUuid(x.uuid())), x.description())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteGroup(UUID uuid) {
        return groupConfigurationDao.delete(uuid);
    }

    @Override
    public Optional<GroupGetModel> getGroup(UUID uuid) {
        var dbResult = groupConfigurationDao.findByUuid(uuid);
        var services = serviceConfigurationDao.findByGroupUuid(uuid);

        return dbResult.map(x -> new GroupGetModel(x.uuid(), x.name(), x.displayOrder(), getServiceUuidList(services), x.description()));
    }

    @Override
    @Transactional
    public boolean patchGroup(UUID groupUuid, List<UUID> serviceList) {
        var oldServices = serviceConfigurationDao.findByGroupUuid(groupUuid);
        var oldServicesUuid = getServiceUuidList(oldServices);
        var group = groupConfigurationDao.findByUuid(groupUuid);
        boolean successGroup = group.isPresent();
        //removing services not in serviceList
        for (ServiceConfigurationEntityWithGroupUuid serviceEntity : oldServices) {
            if (!serviceList.contains(serviceEntity.uuid())) {
                var updateSuccess = serviceConfigurationDao.updateByUuid(new ServiceConfigurationEntity(serviceEntity.id(), serviceEntity.uuid(), serviceEntity.service(), serviceEntity.name(), serviceEntity.ignoreServiceName(), groupConfigurationDao.findDefaultGroupId(), serviceEntity.description()));
                if (!updateSuccess) {
                    throw new InvalidDataException("Service not found: %s".formatted(serviceEntity));
                }
            }
        }
        //adding services from serviceList
        for (UUID serviceUuid : serviceList) {
            if (!oldServicesUuid.contains(serviceUuid) && successGroup) {
                var service = serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid).orElseThrow(() -> new InvalidDataException("Service not found: %s".formatted(serviceUuid)));
                var updateSuccess = serviceConfigurationDao.updateByUuid(new ServiceConfigurationEntity(service.id(), service.uuid(), service.service(), service.name(), service.ignoreServiceName(), group.get().id(), service.description()));
                if (!updateSuccess) {
                    throw new InvalidDataException("Service not found: %s".formatted(serviceUuid));
                }
            }
        }
        return successGroup;
    }

    @Override
    @Transactional
    public Optional<List<ServiceModel>> getServicesInGroup(UUID uuid) {
        if (groupConfigurationDao.findByUuid(uuid).isEmpty()) {
            return Optional.empty();
        }
        var dbResult = serviceConfigurationDao.findByGroupUuid(uuid);
        List<ServiceModel> serviceList = new ArrayList<>();

        for (ServiceConfigurationEntityWithGroupUuid service : dbResult) {
            var model = new ServiceModel(service.name(), service.service(), service.ignoreServiceName(), uuid, service.uuid(), service.description());
            serviceList.add(model);
        }

        return Optional.of(serviceList);
    }

    private List<UUID> getServiceUuidList(List<ServiceConfigurationEntityWithGroupUuid> serviceList){
        List<UUID> uuidList = new ArrayList<>();
        for (ServiceConfigurationEntityWithGroupUuid service : serviceList) {
            uuidList.add(service.uuid());
        }
        return uuidList;
    }
}
