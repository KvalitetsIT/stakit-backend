package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceStatusDao {
    long insert(ServiceStatusEntity serviceStatusEntity);

    List<ServiceStatusEntity> findAll();

    Optional<ServiceStatusEntity> findLatest(String service);

    Optional<ServiceStatusEntity> findById(long id);

    boolean deleteFromServiceConfigurationUuid(UUID uuid);

    Optional<ServiceStatusEntity> findLatestBefore(UUID serviceUuid, OffsetDateTime time);

    List<ServiceStatusEntity> findFrom(UUID serviceUuid, OffsetDateTime from);
}