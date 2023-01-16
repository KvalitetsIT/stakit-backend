package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.model.UpdateServiceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class StatusUpdateServiceImpl implements StatusUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(StatusUpdateServiceImpl.class);
    private final ServiceConfigurationDao serviceConfigurationDao;
    private final ServiceStatusDao serviceStatusDao;
    private final MailQueueService mailQueueService;
    private final GroupConfigurationDao groupConfigurationDao;

    public StatusUpdateServiceImpl(ServiceConfigurationDao serviceConfigurationDao,
                                   ServiceStatusDao serviceStatusDao,
                                   MailQueueService mailQueueService,
                                   GroupConfigurationDao groupConfigurationDao) {
        this.serviceConfigurationDao = serviceConfigurationDao;
        this.serviceStatusDao = serviceStatusDao;
        this.mailQueueService = mailQueueService;
        this.groupConfigurationDao = groupConfigurationDao;
    }

    @Override
    @Transactional
    public void updateStatus(UpdateServiceModel input) {
        Long statusConfigurationId;
        try {
            var groupId = groupConfigurationDao.findDefaultGroupId();
            statusConfigurationId = serviceConfigurationDao.insert(ServiceConfigurationEntity.createInstance(input.service(), UUID.randomUUID(), input.serviceName(), false, groupId, null));
        }
        catch(DuplicateKeyException e) {
            statusConfigurationId = serviceConfigurationDao.findByService(input.service()).id();
        }

        var oldServiceStatus = serviceStatusDao.findLatest(input.service());

        var serviceStatusId = serviceStatusDao.insert(ServiceStatusEntity.createInstance(statusConfigurationId, input.status().toString(), input.statusDateTime(), input.message()));

        if(oldServiceStatus.isPresent() && !oldServiceStatus.get().status().equals(input.status().toString())) {
            logger.info("Service status changed for service {}. Queueing mail send.", input.service());
            try {
                mailQueueService.queueStatusUpdatedMail(statusConfigurationId, serviceStatusId);
            }
            catch(Exception e) {
                // Catch all exceptions from mail sending and just log it.
                logger.error("Error during mail sending.", e);
            }
        }
    }
}
