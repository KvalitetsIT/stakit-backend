package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;

import java.util.List;

public interface ServiceConfigurationDao {
    long insert(ServiceConfigurationEntity serviceConfigurationEntity);

    List<ServiceConfigurationEntity> findAll();

    ServiceConfigurationEntity findByService(String service);
}