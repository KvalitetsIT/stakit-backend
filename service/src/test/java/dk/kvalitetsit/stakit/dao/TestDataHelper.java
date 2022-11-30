package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

public class TestDataHelper {
    @Autowired
    private StatusConfigurationDao statusConfigurationDao;

    @Autowired
    private StatusDao statusDao;

    @Autowired
    private GroupConfigurationDao groupConfigurationDao;

    long createServiceConfiguration(String service, String serviceName, boolean ignoreServiceName) {
        return statusConfigurationDao.insert(StatusConfigurationEntity.createInstance(service, serviceName, ignoreServiceName, null));
    }

    long createServiceConfiguration(String service, String serviceName, boolean ignoreServiceName, long groupConfigurationId) {
        return statusConfigurationDao.insert(StatusConfigurationEntity.createInstance(service, serviceName, ignoreServiceName, groupConfigurationId));
    }

    void createService(long statusConfigurationId, String status, OffsetDateTime statusTime)  {
        statusDao.insertUpdate(StatusEntity.createInstance(statusConfigurationId, status, statusTime, null));
    }

    long createGroup(String groupName) {
        return groupConfigurationDao.insert(GroupConfigurationEntity.createInstance(groupName));
    }
}
