package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDataHelper {
    @Autowired
    private StatusConfigurationDao statusConfigurationDao;

    long createServiceConfiguration(String service, String serviceName, boolean ignoreServiceName) {
        return statusConfigurationDao.insert(StatusConfigurationEntity.createInstance(service, serviceName, ignoreServiceName));
    }
}
