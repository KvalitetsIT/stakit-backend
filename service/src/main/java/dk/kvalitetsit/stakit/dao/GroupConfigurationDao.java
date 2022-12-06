package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupConfigurationDao {
    long insert(GroupConfigurationEntity groupConfigurationEntity);

    List<GroupConfigurationEntity> findAll();

    boolean update(GroupConfigurationEntity groupConfigurationEntity);

    Optional<GroupConfigurationEntity> findByUuid(UUID group);
}
