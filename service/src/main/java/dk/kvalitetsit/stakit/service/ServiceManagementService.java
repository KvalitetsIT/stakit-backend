package dk.kvalitetsit.stakit.service;


import dk.kvalitetsit.stakit.service.model.ServiceModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceManagementService {
    List<ServiceModel> getServices();

    Optional<ServiceModel> getService(UUID uuid);

    boolean updateService(UUID uuid, ServiceModel serviceModel);

    UUID createService(ServiceModel serviceModel);

    boolean deleteService(UUID uuid);
}
