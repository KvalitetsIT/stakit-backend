package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class StatusUpdateServiceImpl implements StatusUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(StatusUpdateServiceImpl.class);
    private final ServiceConfigurationDao serviceConfigurationDao;
    private final ServiceStatusDao serviceStatusDao;
    private final MailService mailService;

    public StatusUpdateServiceImpl(ServiceConfigurationDao serviceConfigurationDao, ServiceStatusDao serviceStatusDao, MailService mailService) {
        this.serviceConfigurationDao = serviceConfigurationDao;
        this.serviceStatusDao = serviceStatusDao;
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public void updateStatus(UpdateServiceInput input) {
        Long statusConfigurationId;
        try {
            statusConfigurationId = serviceConfigurationDao.insert(ServiceConfigurationEntity.createInstance(input.service(), UUID.randomUUID(), input.serviceName(), false, null));
        }
        catch(DuplicateKeyException e) {
            statusConfigurationId = serviceConfigurationDao.findByService(input.service()).id();
        }

        var oldServiceStatus = serviceStatusDao.findLatest(input.service());

        var serviceStatusId = serviceStatusDao.insert(ServiceStatusEntity.createInstance(statusConfigurationId, input.status().toString(), input.statusDateTime(), input.message()));

        if(oldServiceStatus.isPresent() && !oldServiceStatus.get().status().equals(input.status().toString())) {
            logger.info("Service status changed for service {}. Queueing mail send.", input.service());
            mailService.queueStatusUpdatedMail(statusConfigurationId, serviceStatusId);
        }
    }
}
