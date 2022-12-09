package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;

import java.util.List;
import java.util.Optional;

public interface ServiceStatusDao {
    long insert(ServiceStatusEntity serviceStatusEntity);

    List<ServiceStatusEntity> findAll();

    Optional<ServiceStatusEntity> findLatest(String service);
}