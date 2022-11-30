package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.StatusConfigurationDao;
import dk.kvalitetsit.stakit.dao.StatusDao;
import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.StatusEntity;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

public class StatusUpdateServiceImpl implements StatusUpdateService {
    private final StatusConfigurationDao statusConfigurationDao;
    private final StatusDao statusDao;

    public StatusUpdateServiceImpl(StatusConfigurationDao statusConfigurationDao, StatusDao statusDao) {
        this.statusConfigurationDao = statusConfigurationDao;
        this.statusDao = statusDao;
    }

    @Override
    @Transactional
    public void updateStatus(UpdateServiceInput input) {
        Long statusConfigurationId;
        try {
            statusConfigurationId = statusConfigurationDao.insert(StatusConfigurationEntity.createInstance(input.service(), input.serviceName(), false, null));
        }
        catch(DuplicateKeyException e) {
            statusConfigurationId = statusConfigurationDao.findByService(input.service()).id();
        }

        statusDao.insertUpdate(StatusEntity.createInstance(statusConfigurationId, input.status().toString(), input.statusDateTime(), input.message()));
    }
}
