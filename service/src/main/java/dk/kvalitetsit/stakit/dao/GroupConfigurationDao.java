package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;

import java.util.List;

public interface GroupConfigurationDao {
    long insert(GroupConfigurationEntity groupConfigurationEntity);

    List<GroupConfigurationEntity> findAll();
}
