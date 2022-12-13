package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceConfigurationDao {
    long insert(ServiceConfigurationEntity serviceConfigurationEntity);

    List<ServiceConfigurationEntity> findAll();

    ServiceConfigurationEntity findByService(String service);

    Optional<ServiceConfigurationEntityWithGroupUuid> findByUuidWithGroupUuid(UUID serviceUuid);

    boolean updateByUuid(ServiceConfigurationEntity serviceConfigurationEntity);

    List<ServiceConfigurationEntityWithGroupUuid> findAllWithGroupId();

    Optional<ServiceConfigurationEntity> findById(long id);
}