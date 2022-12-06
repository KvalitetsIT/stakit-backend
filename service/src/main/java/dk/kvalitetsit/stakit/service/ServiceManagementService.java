package dk.kvalitetsit.stakit.service;


import dk.kvalitetsit.stakit.service.model.Service;

import java.util.List;
import java.util.UUID;

public interface ServiceManagementService {
    List<Service> getServices();

    Service getService(UUID uuid);

    boolean updateService(UUID uuid, Service service);

    UUID createService(Service service);
}
