package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;

import java.util.List;

public interface StatusConfigurationDao {
    long insert(StatusConfigurationEntity statusConfigurationEntity);

    List<StatusConfigurationEntity> findAll();

    StatusConfigurationEntity findByService(String service);
}