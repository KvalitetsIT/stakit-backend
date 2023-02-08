package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TestDataHelper {
    @Autowired
    private ServiceConfigurationDao serviceConfigurationDao;

    @Autowired
    private ServiceStatusDao serviceStatusDao;

    @Autowired
    private MailSubscriptionDao mailSubscriptionDao;

    @Autowired
    private MailSubscriptionGroupDao mailSubscriptionGroupDao;

    @Autowired
    private GroupConfigurationDao groupConfigurationDao;

    long createServiceConfiguration(UUID serviceUuid, String service, String serviceName, boolean ignoreServiceName, long groupConfigurationId, String status, String description) {
        return serviceConfigurationDao.insert(ServiceConfigurationEntity.createInstance(service, serviceUuid, serviceName, ignoreServiceName, groupConfigurationId, status, description));
    }

    long createServiceConfiguration(String service, String serviceName, boolean ignoreServiceName, long groupConfigurationId, String status, String description) {
        return createServiceConfiguration(UUID.randomUUID(), service, serviceName, ignoreServiceName, groupConfigurationId,  status, description);
    }

    long createServiceConfiguration(String service, String serviceName, boolean ignoreServiceName, Long groupConfigurationId, UUID serviceUuid, String status, String description) {
        return serviceConfigurationDao.insert(ServiceConfigurationEntity.createInstance(service, serviceUuid, serviceName, ignoreServiceName, groupConfigurationId, status, description));
    }

    void createServiceStatus(long statusConfigurationId, String status, OffsetDateTime statusTime)  {
        serviceStatusDao.insert(ServiceStatusEntity.createInstance(statusConfigurationId, status, statusTime, null));
    }

    long createGroup(String groupName, UUID groupUuid, String description) {
        return groupConfigurationDao.insert(GroupConfigurationEntity.createInstance(groupUuid, groupName, 100, description));
    }

    public long createMailSubscription(boolean confirmed, UUID confirmationIdentifier) {
        return mailSubscriptionDao.insert(MailSubscriptionEntity.createInstance(UUID.randomUUID(), "email", true, true, confirmationIdentifier));
    }

    public void createMailSubscriptionGroup(long mailSubscriptionId, long groupCOnfigurationId) {
        mailSubscriptionGroupDao.insert(MailSubscriptionGroupsEntity.createInstance(mailSubscriptionId, groupCOnfigurationId));
    }

    public long findDefaultGroupId() {
        return groupConfigurationDao.findDefaultGroupId();
    }
}
