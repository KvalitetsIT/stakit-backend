package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.service.model.GroupGetModel;
import dk.kvalitetsit.stakit.service.model.GroupModel;
import org.springframework.transaction.annotation.Transactional;

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

        groupConfigurationDao.insert(GroupConfigurationEntity.createInstance(uuid, groupModel.name(), groupModel.displayOrder()));

        return uuid;
    }

    @Override
    @Transactional
    public boolean updateGroup(GroupModel groupModel) {
        return groupConfigurationDao.update(GroupConfigurationEntity.createInstance(groupModel.uuid(), groupModel.name(), groupModel.displayOrder()));
    }

    @Override
    @Transactional
    public List<GroupGetModel> getGroups() {
        var dbResult = groupConfigurationDao.findAll();

        return dbResult.stream().map(x -> new GroupGetModel(x.uuid(), x.name(), x.displayOrder(), serviceConfigurationDao.findByGroupUuid(x.uuid()))).collect(Collectors.toList());
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

        return dbResult.map(x -> new GroupGetModel(x.uuid(), x.name(), x.displayOrder(), services));
    }
}
