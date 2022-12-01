package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TestDataHelper {
    @Autowired
    private ServiceConfigurationDao serviceConfigurationDao;

    @Autowired
    private ServiceStatusDao serviceStatusDao;

    @Autowired
    private GroupConfigurationDao groupConfigurationDao;

    long createServiceConfiguration(String service, String serviceName, boolean ignoreServiceName) {
        return serviceConfigurationDao.insert(ServiceConfigurationEntity.createInstance(service, serviceName, ignoreServiceName, null));
    }

    long createServiceConfiguration(String service, String serviceName, boolean ignoreServiceName, long groupConfigurationId) {
        return serviceConfigurationDao.insert(ServiceConfigurationEntity.createInstance(service, serviceName, ignoreServiceName, groupConfigurationId));
    }

    void createService(long statusConfigurationId, String status, OffsetDateTime statusTime)  {
        serviceStatusDao.insertUpdate(ServiceStatusEntity.createInstance(statusConfigurationId, status, statusTime, null));
    }

    long createGroup(String groupName) {
        return groupConfigurationDao.insert(GroupConfigurationEntity.createInstance(UUID.randomUUID(), groupName, 10));
    }
}
