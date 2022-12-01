package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;

import java.util.List;

public interface ServiceStatusDao {
    void insertUpdate(ServiceStatusEntity serviceStatusEntity);

    List<ServiceStatusEntity> findAll();
}