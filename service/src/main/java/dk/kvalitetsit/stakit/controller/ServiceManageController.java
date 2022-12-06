package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.mapper.ServiceManagementMapper;
import dk.kvalitetsit.stakit.service.ServiceManagementService;
import org.openapitools.api.ServiceManagementApi;
import org.openapitools.model.Service;
import org.openapitools.model.ServiceCreate;
import org.openapitools.model.ServiceUpdate;
import org.openapitools.model.Services;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost") // TODO Jeg skal nok fjernes igen.
public class ServiceManageController implements ServiceManagementApi {
    private final ServiceManagementService serviceManagementService;

    public ServiceManageController(ServiceManagementService serviceManagementService) {
        this.serviceManagementService = serviceManagementService;
    }

    @Override
    public ResponseEntity<List<Services>> v1ServicesGet() {
        var services = serviceManagementService.getServices();
        return ResponseEntity.ok(ServiceManagementMapper.mapServices(services));
    }

    @Override
    public ResponseEntity<Void> v1ServicesPost(ServiceCreate serviceCreate) {
        var serviceUuid = serviceManagementService.createService(ServiceManagementMapper.mapCreate(serviceCreate));

        return ResponseEntity.status(HttpStatus.CREATED).header("Location", serviceUuid.toString()).build(); // TODO Jeg skal nok være en rigtig URL.
    }

    @Override
    public ResponseEntity<Service> v1ServicesUuidGet(UUID uuid) {
        var service  = serviceManagementService.getService(uuid);

        return service == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(ServiceManagementMapper.mapService(service));
    }

    @Override
    public ResponseEntity<Void> v1ServicesUuidPut(UUID uuid, ServiceUpdate serviceUpdate) {
        boolean updated = serviceManagementService.updateService(uuid, ServiceManagementMapper.mapUpdate(serviceUpdate));

        return updated ? ResponseEntity.status(201).build() : ResponseEntity.notFound().build();
    }
}
