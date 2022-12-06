package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class StatusUpdateServiceImpl implements StatusUpdateService {
    private final ServiceConfigurationDao serviceConfigurationDao;
    private final ServiceStatusDao serviceStatusDao;

    public StatusUpdateServiceImpl(ServiceConfigurationDao serviceConfigurationDao, ServiceStatusDao serviceStatusDao) {
        this.serviceConfigurationDao = serviceConfigurationDao;
        this.serviceStatusDao = serviceStatusDao;
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

        serviceStatusDao.insertUpdate(ServiceStatusEntity.createInstance(statusConfigurationId, input.status().toString(), input.statusDateTime(), input.message()));
    }
}
