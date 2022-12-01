package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.service.model.Group;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupServiceImpl implements GroupService {
    private final GroupConfigurationDao groupConfigurationDao;

    public GroupServiceImpl(GroupConfigurationDao groupConfigurationDao) {
        this.groupConfigurationDao = groupConfigurationDao;
    }

    @Override
    public UUID createGroup(Group group) {
        var uuid = UUID.randomUUID();

        groupConfigurationDao.insert(GroupConfigurationEntity.createInstance(uuid, group.name(), group.displayOrder()));

        return uuid;
    }

    @Override
    public boolean updateGroup(Group group) {
        return groupConfigurationDao.update(GroupConfigurationEntity.createInstance(group.uuid(), group.name(), group.displayOrder()));
    }

    @Override
    public List<Group> getGroups() {
        var dbResult = groupConfigurationDao.findAll();

        return dbResult.stream().map(x -> new Group(x.uuid(), x.name(), x.displayOrder())).collect(Collectors.toList());
    }
}
