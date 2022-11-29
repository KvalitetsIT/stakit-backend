package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.StatusEntity;
import dk.kvalitetsit.stakit.dao.model.ServiceConfigurationEntity;

import java.util.List;

public interface StatusConfigurationDao {
    long insert(StatusConfigurationEntity statusConfigurationEntity);

    List<StatusConfigurationEntity> findAll();

    StatusConfigurationEntity findByService(String service);
}